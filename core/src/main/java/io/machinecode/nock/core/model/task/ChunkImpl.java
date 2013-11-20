package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.ChunkFactory;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.deferred.DeferredImpl;
import io.machinecode.nock.core.work.RepositoryStatus;
import io.machinecode.nock.spi.Checkpoint;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.batch.api.chunk.CheckpointAlgorithm;
import javax.batch.api.chunk.ItemProcessor;
import javax.batch.api.chunk.ItemReader;
import javax.batch.api.chunk.ItemWriter;
import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.BatchStatus;
import javax.transaction.Status;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static javax.batch.runtime.Metric.MetricType.COMMIT_COUNT;
import static javax.batch.runtime.Metric.MetricType.FILTER_COUNT;
import static javax.batch.runtime.Metric.MetricType.PROCESS_SKIP_COUNT;
import static javax.batch.runtime.Metric.MetricType.READ_COUNT;
import static javax.batch.runtime.Metric.MetricType.READ_SKIP_COUNT;
import static javax.batch.runtime.Metric.MetricType.ROLLBACK_COUNT;
import static javax.batch.runtime.Metric.MetricType.WRITE_COUNT;
import static javax.batch.runtime.Metric.MetricType.WRITE_SKIP_COUNT;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkImpl extends DeferredImpl<ExecutionContext, Throwable> implements Chunk, TaskWork {

    private static final Logger log = Logger.getLogger(ChunkImpl.class);

    private final String checkpointPolicy;
    private final String itemCount;
    private final String timeLimit;
    private final String skipLimit;
    private final String retryLimit;
    private final ItemReaderImpl reader;
    private final ItemProcessorImpl processor;
    private final ItemWriterImpl writer;
    private final CheckpointAlgorithmImpl checkpointAlgorithm;
    private final ExceptionClassFilterImpl skippableExceptionClasses;
    private final ExceptionClassFilterImpl retryableExceptionClasses;
    private final ExceptionClassFilterImpl noRollbackExceptionClasses;
    private final ListenersImpl listeners;
    private final PartitionImpl<?> partition;

    public ChunkImpl(
        final String checkpointPolicy,
        final String itemCount,
        final String timeLimit,
        final String skipLimit,
        final String retryLimit,
        final ItemReaderImpl reader,
        final ItemProcessorImpl processor,
        final ItemWriterImpl writer,
        final CheckpointAlgorithmImpl checkpointAlgorithm,
        final ExceptionClassFilterImpl skippableExceptionClasses,
        final ExceptionClassFilterImpl retryableExceptionClasses,
        final ExceptionClassFilterImpl noRollbackExceptionClasses,
        final ListenersImpl listeners,
        final PartitionImpl<?> partition
    ) {
        this.checkpointPolicy = checkpointPolicy;
        this.itemCount = itemCount;
        this.timeLimit = timeLimit;
        this.skipLimit = skipLimit;
        this.retryLimit = retryLimit;
        this.reader = reader;
        this.processor = processor;
        this.writer = writer;
        this.checkpointAlgorithm = checkpointAlgorithm;
        this.skippableExceptionClasses = skippableExceptionClasses;
        this.retryableExceptionClasses = retryableExceptionClasses;
        this.noRollbackExceptionClasses = noRollbackExceptionClasses;
        this.listeners = listeners;
        this.partition = partition;
    }

    @Override
    public String getCheckpointPolicy() {
        return this.checkpointPolicy;
    }

    @Override
    public String getItemCount() {
        return this.itemCount;
    }

    @Override
    public String getTimeLimit() {
        return this.timeLimit;
    }

    @Override
    public String getSkipLimit() {
        return this.skipLimit;
    }

    @Override
    public String getRetryLimit() {
        return this.retryLimit;
    }

    @Override
    public ItemReaderImpl getReader() {
        return this.reader;
    }

    @Override
    public ItemProcessorImpl getProcessor() {
        return this.processor;
    }

    @Override
    public ItemWriterImpl getWriter() {
        return this.writer;
    }

    @Override
    public CheckpointAlgorithmImpl getCheckpointAlgorithm() {
        return this.checkpointAlgorithm;
    }

    @Override
    public ExceptionClassFilterImpl getSkippableExceptionClasses() {
        return this.skippableExceptionClasses;
    }

    @Override
    public ExceptionClassFilterImpl getRetryableExceptionClasses() {
        return this.retryableExceptionClasses;
    }

    @Override
    public ExceptionClassFilterImpl getNoRollbackExceptionClasses() {
        return this.noRollbackExceptionClasses;
    }

    // Lifecycle

    private static final int BEGIN = 1;
    private static final int READ = 2;
    private static final int PROCESS = 3;
    private static final int ADD = 4;
    private static final int WRITE = 5;
    private static final int COMMIT = 6;

    private class State {
        int next = BEGIN;
        boolean finished = false;
        Object value;
        Serializable readInfo = null;
        Serializable writeInfo = null;
        Exception exception = null;
        Throwable failure = null;

        final List<Object> objects;

        final long jobExecutionId;
        final TransactionManager transactionManager;
        final ExecutionRepository repository;
        final MutableJobContext jobContext;
        final MutableStepContext stepContext;
        final ExecutionContext context;

        final ArrayList<Item> items;

        final int itemCount;
        final int timeLimit;
        final int skipLimit;
        final int retryLimit;

        final ItemReader reader;
        final ItemProcessor processor;
        final ItemWriter writer;
        final CheckpointAlgorithm checkpointAlgorithm;
        final List<ChunkListener> chunkListeners;
        final List<ItemReadListener> itemReadListeners;
        final List<RetryReadListener> retryReadListeners;
        final List<SkipReadListener> skipReadListeners;
        final List<ItemProcessListener> itemProcessListeners;
        final List<RetryProcessListener> retryProcessListeners;
        final List<SkipProcessListener> skipProcessListeners;
        final List<ItemWriteListener> itemWriteListeners;
        final List<RetryWriteListener> retryWriteListeners;
        final List<SkipWriteListener> skipWriteListeners;

        private State(final Executor executor, final ExecutionContext context, final int timeout) throws Exception {
            this.jobExecutionId = context.getJobExecutionId();
            this.transactionManager = executor.getTransactionManager();
            this.repository = executor.getRepository();
            this.stepContext = context.getStepContext();
            this.jobContext = context.getJobContext();
            this.context = context;
            this.items = new ArrayList<Item>();

            this.itemCount = Integer.parseInt(ChunkImpl.this.itemCount);
            this.timeLimit = Integer.parseInt(ChunkImpl.this.timeLimit);
            this.skipLimit = Integer.parseInt(ChunkImpl.this.skipLimit);
            this.retryLimit = Integer.parseInt(ChunkImpl.this.retryLimit);

            final Checkpoint checkpoint = repository.getStepExecutionCheckpoint(stepContext.getStepExecutionId());
            if (checkpoint != null) {
                this.readInfo = checkpoint.getReaderCheckpoint();
                this.writeInfo = checkpoint.getWriterCheckpoint();
            }

            this.objects = ChunkImpl.this.checkpointAlgorithm == null
                    ? new LinkedList<Object>()
                    : new ArrayList<Object>(this.itemCount);

            this.reader = ChunkImpl.this.reader.load(executor, context);
            if (this.reader == null) {
                throw new IllegalStateException(Messages.format("chunk.reader.null", jobExecutionId, ChunkImpl.this.reader.getRef()));
            }
            this.processor = ChunkImpl.this.processor == null
                    ? null
                    : ChunkImpl.this.processor.load(executor, context);
            this.writer = ChunkImpl.this.writer.load(executor, context);
            if (this.writer == null) {
                throw new IllegalStateException(Messages.format("chunk.writer.null", jobExecutionId, ChunkImpl.this.writer.getRef()));
            }
            this.checkpointAlgorithm = CheckpointPolicy.ITEM.equalsIgnoreCase(ChunkImpl.this.checkpointPolicy) || ChunkImpl.this.checkpointAlgorithm == null //TODO Check this is caught in the job validator and remove this
                    ? new ItemCheckpointAlgorithm(timeout, this.itemCount)
                    : ChunkImpl.this.checkpointAlgorithm.load(executor, context);
            this.chunkListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, ChunkListener.class);
            this.itemReadListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, ItemReadListener.class);
            this.retryReadListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, RetryReadListener.class);
            this.skipReadListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, SkipReadListener.class);
            this.itemProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, ItemProcessListener.class);
            this.retryProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, RetryProcessListener.class);
            this.skipProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, SkipProcessListener.class);
            this.itemWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, ItemWriteListener.class);
            this.retryWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, RetryWriteListener.class);
            this.skipWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(executor, context, SkipWriteListener.class);
        }

        public void next(final int status) {
            this.next = status;
        }

        public void set(final int status, final Object value) {
            this.next = status;
            this.value = value;
        }

        public void throwable(final Throwable e) {
            if (this.failure == null) {
                this.failure = e;
            } else {
                this.failure.addSuppressed(e);
            }
        }

        public void exception(final Exception e) {
            if (this.exception == null) {
                this.exception = e;
            } else {
                this.exception.addSuppressed(e);
            }
        }

        public boolean failed() {
            return this.exception != null || this.failure != null;
        }

        public void tryThrow(final String message, final DeferredImpl<?,Throwable> deferred) throws Exception {
            if (this.exception != null) {
                if (this.failure != null) {
                    this.exception.addSuppressed(failure);
                }
                throw this.exception;
            }
            if (this.failure != null) {
                final BatchRuntimeException e = new BatchRuntimeException(Messages.format(message, this.jobExecutionId), this.failure);
                deferred.reject(e);
                throw e;
            }
        }
    }

    @Override
    public TaskWork partition(final PropertyContext context) {
        return ChunkFactory.INSTANCE.producePartitioned(this, this.listeners, this.partition, context);
    }

    @Override
    public boolean isPartitioned() {
        return partition != null;
    }

    @Override
    public void run(final Executor executor, final ExecutionContext context, final int timeout) throws Exception {
        final State state = new State(
                executor,
                context,
                timeout
        );

        log.debugf(Messages.get("chunk.transaction.timeout"), state.jobExecutionId, timeout);
        state.transactionManager.setTransactionTimeout(timeout);
        state.transactionManager.begin();
        _openReader(state);
        _openWriter(state);
        try {
            if (!state.failed()) {
                state.transactionManager.commit();
            } else {
                state.transactionManager.setRollbackOnly();
                _closeReader(state);
                _closeWriter(state);
                state.transactionManager.rollback();
            }
        } catch (final Throwable e) {
            state.throwable(e);
        }
        state.tryThrow("chunk.failed.opening", this);

        try {
            try {
                while (!state.failed() && _loop(state, executor, context)) {
                    //
                }
            } catch (final Exception e) {
                state.exception(e);
                state.transactionManager.setRollbackOnly();
                _runErrorListeners(state, e);
                log.infof(e, Messages.format("chunk.exception", state.jobExecutionId));
            } catch (final Throwable e) {
                state.throwable(e);
                state.transactionManager.setRollbackOnly();
                log.infof(e, Messages.format("chunk.throwable", state.jobExecutionId));
            }
        } catch (final Throwable e) {
            state.throwable(e);
        } finally {
            try {
                if (state.failed() && state.transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION) {
                    state.transactionManager.rollback();
                }
            } catch (final Throwable e) {
                state.throwable(e);
            }
        }

        state.transactionManager.begin();
        _closeReader(state);
        _closeWriter(state);
        if (!state.failed()) {
            _collect(state, executor, context, BatchStatus.FAILED, state.stepContext.getExitStatus()); //TODO
            state.transactionManager.commit();
        } else {
            state.transactionManager.rollback();
        }
        state.tryThrow("chunk.failed", this);
        if (isCancelled()) {
            context.getStepContext().setBatchStatus(BatchStatus.STOPPED);
            context.getJobContext().setBatchStatus(BatchStatus.STOPPED);
            RepositoryStatus.stoppedJob(state.repository, state.jobExecutionId, state.stepContext.getExitStatus());
        }
        context.setItems(state.items.toArray(new Item[state.items.size()]));
        //TODO These should be in a finally block
        resolve(context);
    }

    private boolean _loop(final State state, final Executor executor, final ExecutionContext context) throws Exception {
        if (BatchStatus.STOPPING.equals(state.stepContext.getBatchStatus())) {
            cancel(true);
        }
        switch (state.next) {
            case BEGIN:
                final int checkpointTimeout = state.checkpointAlgorithm.checkpointTimeout();
                log.debugf(Messages.get("chunk.transaction.timeout"), state.jobExecutionId, checkpointTimeout);
                state.transactionManager.setTransactionTimeout(checkpointTimeout);
                log.debugf(Messages.get("chunk.state.begin"), state.jobExecutionId);
                state.checkpointAlgorithm.beginCheckpoint();
                state.transactionManager.begin();
                _runBeforeListeners(state);
                state.next(_isReadyToCheckpoint(state) ? WRITE : READ);
                break;
            case READ:
                log.debugf(Messages.get("chunk.state.read"), state.jobExecutionId);
                _readItem(state);
                break;
            case PROCESS:
                log.debugf(Messages.get("chunk.state.process"), state.jobExecutionId);
                _processItem(state.value, state);
                break;
            case ADD:
                log.debugf(Messages.get("chunk.state.add"), state.jobExecutionId);
                state.objects.add(state.value);
                state.next(_isReadyToCheckpoint(state) ? WRITE : READ);
                break;
            case WRITE:
                log.debugf(Messages.get("chunk.state.write"), state.jobExecutionId);
                _writeItem(state);
                // 11.8 9 m has this outside the write catch block
                _runAfterListeners(state);
                break;
            case COMMIT:
                log.debugf(Messages.get("chunk.state.commit"), state.jobExecutionId);
                log.debugf(Messages.get("chunk.reader.checkpoint"), state.jobExecutionId, reader.getRef());
                state.readInfo = state.reader.checkpointInfo();
                log.debugf(Messages.get("chunk.writer.checkpoint"), state.jobExecutionId, writer.getRef());
                state.writeInfo = state.writer.checkpointInfo();

                state.repository.updateStepExecution(
                        state.stepContext.getStepExecutionId(),
                        state.stepContext.getPersistentUserData(),
                        state.stepContext.getMetrics(),
                        new CheckpointImpl(state.readInfo, state.writeInfo),
                        new Date()
                );
                state.transactionManager.commit();
                state.stepContext.getMetric(COMMIT_COUNT).increment();
                state.checkpointAlgorithm.endCheckpoint();
                _collect(state, executor, context, state.stepContext.getBatchStatus(), state.stepContext.getExitStatus()); //TODO
                if (isCancelled()) {
                    return false;
                }
                if (state.finished) {
                    return false;
                }
                state.next(BEGIN);
                break;
            default:
                throw new IllegalStateException();
        }
        return true;
    }

    private boolean _isReadyToCheckpoint(final State state) throws Exception {
        return state.checkpointAlgorithm.isReadyToCheckpoint() || isCancelled();
    }

    private void _collect(final State state, final Executor executor, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) {
        try {
            if (partition != null) {
                state.items.add(partition.collect(this, executor, context, batchStatus, exitStatus));
            }
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Messages.format("chunk.partition.exception.collect", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Messages.format("chunk.partition.throwable.collect", state.jobExecutionId));
        }
    }

    private void _openReader(final State state) {
        try {
            log.debugf(Messages.get("chunk.reader.open"), state.jobExecutionId, reader.getRef());
            state.reader.open(state.readInfo);
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Messages.format("chunk.reader.exception.opening", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Messages.format("chunk.reader.throwable.opening", state.jobExecutionId));
        }
    }

    private void _closeReader(final State state) {
        try {
            log.debugf(Messages.get("chunk.reader.close"), state.jobExecutionId, reader.getRef());
            state.reader.close();
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Messages.format("chunk.reader.exception.closing", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Messages.format("chunk.reader.throwable.closing", state.jobExecutionId));
        }
    }

    private void _openWriter(final State state) {
        try {
            log.debugf(Messages.get("chunk.writer.open"), state.jobExecutionId, reader.getRef());
            state.writer.open(state.writeInfo);
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Messages.format("chunk.writer.exception.opening", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Messages.format("chunk.writer.throwable.opening", state.jobExecutionId));
        }
    }

    private void _closeWriter(final State state) {
        try {
            log.debugf(Messages.get("chunk.writer.close"), state.jobExecutionId, writer.getRef());
            state.writer.close();
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Messages.format("chunk.writer.exception.closing", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Messages.format("chunk.writer.throwable.closing", state.jobExecutionId));
        }
    }

    private void _runBeforeListeners(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("chunk.listener.before"), state.jobExecutionId);
                listener.beforeChunk();
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private void _runAfterListeners(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("chunk.listener.after"), state.jobExecutionId);
                listener.afterChunk();
            } catch (final Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private void _runErrorListeners(final State state, final Exception that) throws Exception {
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("chunk.listener.error"), state.jobExecutionId);
                listener.onError(that);
            } catch (final Exception e) {
                state.exception(e);
            }
        }
    }

    private void _readItem(final State state) throws Exception {
        try {
            for (final ItemReadListener listener : state.itemReadListeners) {
                log.debugf(Messages.get("chunk.reader.before"), state.jobExecutionId);
                listener.beforeRead();
            }
            log.debugf(Messages.get("chunk.reader.read"), state.jobExecutionId, reader.getRef());
            final Object read = state.reader.readItem();
            if (read == null) {
                state.finished = true;
                state.next(WRITE);
                return;
            }
            state.stepContext.getMetric(READ_COUNT).increment();
            for (final ItemReadListener listener : state.itemReadListeners) {
                log.debugf(Messages.get("chunk.reader.after"), state.jobExecutionId);
                listener.afterRead(read);
            }
            state.set(state.processor == null ? ADD : PROCESS, read);
        } catch (final Exception e) {
            for (final ItemReadListener listener : state.itemReadListeners) {
                log.debugf(Messages.get("chunk.reader.error"), state.jobExecutionId);
                listener.onReadError(e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                state.stepContext.getMetric(READ_SKIP_COUNT).increment();
                for (final SkipReadListener listener : state.skipReadListeners) {
                    log.debugf(Messages.get("chunk.reader.skip"), state.jobExecutionId);
                    listener.onSkipReadItem(e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryReadListener listener : state.retryReadListeners) {
                    log.debugf(Messages.get("chunk.reader.retry"), state.jobExecutionId);
                    listener.onRetryReadException(e);
                }
                if (getNoRollbackExceptionClasses().matches(e)) {
                    state.next(READ);
                    return;
                }
                _rollbackTransaction(state);
                return;
            }
            throw e;
        }
    }

    private void _processItem(final Object read, final State state) throws Exception {
        try {
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                log.debugf(Messages.get("chunk.processor.before"), state.jobExecutionId);
                listener.beforeProcess(read);
            }
            log.debugf(Messages.get("chunk.processor.process"), state.jobExecutionId, processor.getRef());
            final Object processed = state.processor == null ? read : state.processor.processItem(read);
            if (processed == null) {
                state.stepContext.getMetric(FILTER_COUNT).increment();
                state.next(_isReadyToCheckpoint(state) ? WRITE : READ);
                return;
            }
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                log.debugf(Messages.get("chunk.processor.after"), state.jobExecutionId);
                listener.afterProcess(read, processed);
            }
            state.set(ADD, processed);
        } catch (final Exception e) {
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                log.debugf(Messages.get("chunk.processor.error"), state.jobExecutionId);
                listener.onProcessError(read, e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                state.stepContext.getMetric(PROCESS_SKIP_COUNT).increment();
                for (final SkipProcessListener listener : state.skipProcessListeners) {
                    log.debugf(Messages.get("chunk.processor.skip"), state.jobExecutionId);
                    listener.onSkipProcessItem(read, e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryProcessListener listener : state.retryProcessListeners) {
                    log.debugf(Messages.get("chunk.processor.retry"), state.jobExecutionId);
                    listener.onRetryProcessException(read, e);
                }
                _rollbackTransaction(state);
                return;
            }
            throw e;
        }
    }

    private void _writeItem(final State state) throws Exception {
        try {
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                log.debugf(Messages.get("chunk.writer.before"), state.jobExecutionId);
                listener.beforeWrite(state.objects);
            }
            log.debugf(Messages.get("chunk.writer.write"), state.jobExecutionId, writer.getRef());
            state.writer.writeItems(state.objects);
            state.stepContext.getMetric(WRITE_COUNT).increment();
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                log.debugf(Messages.get("chunk.writer.after"), state.jobExecutionId);
                listener.afterWrite(state.objects);
            }
            state.next(COMMIT);
        } catch (final Exception e) {
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                log.debugf(Messages.get("chunk.writer.error"), state.jobExecutionId);
                listener.onWriteError(state.objects, e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                state.stepContext.getMetric(WRITE_SKIP_COUNT).increment();
                for (final SkipWriteListener listener : state.skipWriteListeners) {
                    log.debugf(Messages.get("chunk.writer.skip"), state.jobExecutionId);
                    listener.onSkipWriteItem(state.objects, e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryWriteListener listener : state.retryWriteListeners) {
                    log.debugf(Messages.get("chunk.writer.retry"), state.jobExecutionId);
                    listener.onRetryWriteException(state.objects, e);
                }
                _rollbackTransaction(state);
                return;
            }
            throw e;
        } finally {
            state.objects.clear();
        }
    }

    private void _rollbackTransaction(final State state) throws Exception {
        state.stepContext.getMetric(ROLLBACK_COUNT).increment();
        try {
            _closeReader(state);
            _closeWriter(state);
        } finally {
            state.transactionManager.rollback();
        }

        state.transactionManager.begin();
            _openReader(state);
            _openWriter(state);
        if (!state.failed()) {
            state.transactionManager.commit();
        } else {
            state.transactionManager.rollback();
        }
        state.objects.clear();
        state.next(BEGIN);
    }

}
