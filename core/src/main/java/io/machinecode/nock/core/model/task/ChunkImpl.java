package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.ChunkFactory;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.deferred.DeferredImpl;
import io.machinecode.nock.core.work.ItemImpl;
import io.machinecode.nock.core.work.Repository;
import io.machinecode.nock.spi.Checkpoint;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.ExecutionContext;
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
import javax.transaction.SystemException;
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
public class ChunkImpl extends DeferredImpl<ExecutionContext> implements Chunk, TaskWork {

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
    private static final int CHECKPOINT = 6;
    private static final int COMMIT = 7;

    private transient volatile ExecutionContext _context;

    @Override
    public TaskWork partition(final PropertyContext context) {
        return ChunkFactory.INSTANCE.producePartitioned(this, this.listeners, this.partition, context);
    }

    @Override
    public void run(final Executor executor, final ExecutionContext context, final int timeout) throws Exception {
        this._context = context;
        final long stepExecutionId = context.getStepExecutionId();
        final Integer partitionId = context.getPartitionId();
        final MutableStepContext stepContext = context.getStepContext();
        final State state;
        try {
            state = new State(
                    executor,
                    context,
                    timeout
            );
        } catch (final Throwable e) {
            reject(e);
            if (partitionId != null) {
                executor.getRepository().finishPartitionExecution(
                        stepExecutionId,
                        partitionId,
                        stepContext.getPersistentUserData(),
                        BatchStatus.FAILED,
                        stepContext.getExitStatus(),
                        new Date()
                );
            }
            return;
        }
        try {
            state.stepContext.setBatchStatus(BatchStatus.STARTED);
            if (partitionId != null) {
                state.repository.updatePartitionExecution(
                        stepExecutionId,
                        partitionId,
                        stepContext.getPersistentUserData(),
                        BatchStatus.STARTED,
                        new Date()
                );
            }

            try {
                log.debugf(Messages.get("NOCK-014100.chunk.transaction.timeout"), this._context, timeout);
                state.transactionManager.setTransactionTimeout(timeout);
                log.debugf(Messages.get("NOCK-014101.chunk.transaction.begin"), this._context);
                state.transactionManager.begin();
                _openReader(state);
                _openWriter(state);
                if (!state.isFailed()) {
                    log.debugf(Messages.get("NOCK-014102.chunk.transaction.commit"), this._context);
                    state.transactionManager.commit();
                } else {
                    _closeReader(state);
                    _closeWriter(state);
                    log.debugf(Messages.get("NOCK-014103.chunk.transaction.rollback"), this._context);
                    state.transactionManager.rollback();
                }
            } catch (final Exception e) {
                _handleException(state, e);
            } catch (final Throwable e) {
                state.setThrowable(e);
            } finally {
                _cleanupTx(state);
            }

            if (state.isFailed()) {
                reject(state.getFailure());
                if (partitionId != null) {
                    state.repository.finishPartitionExecution(
                            stepExecutionId,
                            partitionId,
                            stepContext.getPersistentUserData(),
                            BatchStatus.FAILED,
                            stepContext.getExitStatus(),
                            new Date()
                    );
                }
                return;
            }

            try {
                while (_loop(state, executor, context)) {
                    //
                }
            } catch (final Exception e) {
                _handleException(state, e);
            } catch (final Throwable e) {
                state.setThrowable(e);
            } finally {
                _cleanupTx(state);
            }

            try {
                log.debugf(Messages.get("NOCK-014101.chunk.transaction.begin"), this._context);
                state.transactionManager.begin();
                _closeReader(state);
                _closeWriter(state);
                //TODO Spec says to have this here
                //_collect(state, executor, context, state.stepContext.getBatchStatus(), state.stepContext.getExitStatus());
                context.setItems(state.items.toArray(new Item[state.items.size()]));
                if (!state.isFailed()) {
                    log.debugf(Messages.get("NOCK-014102.chunk.transaction.commit"), this._context);
                    state.transactionManager.commit();
                } else {
                    log.debugf(Messages.get("NOCK-014103.chunk.transaction.rollback"), this._context);
                    state.transactionManager.rollback();
                }
            } catch (final Exception e) {
                _handleExceptionInTransaction(state, e);
            } catch (final Throwable e) {
                state.setThrowable(e);
            } finally {
                _cleanupTx(state);
            }
        } catch (final Throwable e) {
            state.setThrowable(e);
        } finally {
            synchronized (this) {
                final BatchStatus batchStatus;
                if (isCancelled()) {
                    state.stepContext.setBatchStatus(batchStatus = BatchStatus.STOPPING);
                } else if (state.isFailed()) {
                    batchStatus = BatchStatus.FAILED;
                    reject(state.getFailure());
                } else {
                    batchStatus = BatchStatus.COMPLETED;
                    resolve(context);
                }
                if (partitionId != null) {
                    state.repository.finishPartitionExecution(
                            stepExecutionId,
                            partitionId,
                            stepContext.getPersistentUserData(),
                            batchStatus,
                            stepContext.getExitStatus(),
                            new Date()
                    );
                }
            }
        }
    }

    private boolean _loop(final State state, final Executor executor, final ExecutionContext context) throws Exception {
        if (state.isFailed()) {
            return false;
        }
        Exception exception = null;
        switch (state.next) {
            case BEGIN:
                final int checkpointTimeout = state.checkpointAlgorithm.checkpointTimeout();
                log.debugf(Messages.get("NOCK-014100.chunk.transaction.timeout"), this._context, checkpointTimeout);
                state.transactionManager.setTransactionTimeout(checkpointTimeout);
                log.debugf(Messages.get("NOCK-014200.chunk.state.begin"), this._context);
                state.checkpointStartTime = System.currentTimeMillis();
                state.checkpointAlgorithm.beginCheckpoint();
                log.debugf(Messages.get("NOCK-014101.chunk.transaction.begin"), this._context);
                state.transactionManager.begin();
                _runBeforeListeners(state);
                state.next(READ);
                break;
            case READ:
                log.debugf(Messages.get("NOCK-014201.chunk.state.read"), this._context);
                _readItem(state);
                break;
            case PROCESS:
                log.debugf(Messages.get("NOCK-014202.chunk.state.process"), this._context);
                _processItem(state.value, state);
                break;
            case ADD:
                log.debugf(Messages.get("NOCK-014203.chunk.state.add"), this._context);
                state.objects.add(state.value);
                state.next(state.isReadyToCheckpoint() ? WRITE : READ);
                break;
            case WRITE:
                log.debugf(Messages.get("NOCK-014204.chunk.state.write"), this._context);
                _writeItem(state);
                break;
            case CHECKPOINT:
                log.debugf(Messages.get("NOCK-014205.chunk.state.checkpoint"), this._context);
                // 11.8 9 m has this outside the write catch block
                _runAfterListeners(state);
                exception = _checkpoint(state, executor, context);
            case COMMIT:
                log.debugf(Messages.get("NOCK-014206.chunk.state.commit"), this._context);
                _commit(state, exception);
                if (isCancelled()) {
                    return false;
                }
                if (state.finished) {
                    return false;
                }
                state.next(BEGIN);
                break;
            default:
                throw new IllegalStateException(Messages.format("NOCK-014000.chunk.illegal.state", this._context, state.next));
        }
        return true;
    }

    private void _collect(final State state, final Executor executor, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) {
        try {
            if (partition != null) {
                state.items.add(partition.collect(this, executor, context, batchStatus, exitStatus));
            } else {
                state.items.add(new ItemImpl(null, batchStatus, exitStatus));
            }
        } catch (final Exception e) {
            log.warnf(Messages.format("NOCK-014710.chunk.partition.exception.collect", this._context, e.getClass().getCanonicalName()));
            _handleException(state, e);
        } catch (final Throwable e) {
            log.warnf(Messages.format("NOCK-014711.chunk.partition.throwable.collect", this._context, e.getClass().getCanonicalName()));
            state.setThrowable(e);
        }
    }

    private void _openReader(final State state) {
        try {
            log.debugf(Messages.get("NOCK-014400.chunk.reader.open"), this._context, this.reader.getRef());
            state.reader.open(state.readInfo);
        } catch (final Exception e) {
            _handleExceptionInTransaction(state, e);
            log.infof(e, Messages.format("NOCK-014702.chunk.reader.exception.opening", this._context, this.reader.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("NOCK-014703.chunk.reader.throwable.opening", this._context, this.reader.getRef()));
        }
    }

    private void _closeReader(final State state) {
        try {
            log.debugf(Messages.get("NOCK-014401.chunk.reader.close"), this._context, this.reader.getRef());
            state.reader.close();
        } catch (final Exception e) {
            _handleExceptionInTransaction(state, e);
            log.infof(e, Messages.format("NOCK-014706.chunk.reader.exception.closing", this._context, this.reader.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("NOCK-014707.chunk.reader.throwable.closing", this._context, this.reader.getRef()));
        }
    }

    private void _openWriter(final State state) {
        try {
            log.debugf(Messages.get("NOCK-014600.chunk.writer.open"), this._context, this.writer.getRef());
            state.writer.open(state.writeInfo);
        } catch (final Exception e) {
            _handleExceptionInTransaction(state, e);
            log.infof(e, Messages.format("NOCK-014704.chunk.writer.exception.opening", this._context, this.writer.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("NOCK-014705.chunk.writer.throwable.opening", this._context, this.writer.getRef()));
        }
    }

    private void _closeWriter(final State state) {
        try {
            log.debugf(Messages.get("NOCK-014601.chunk.writer.close"), this._context, this.writer.getRef());
            state.writer.close();
        } catch (final Exception e) {
            _handleExceptionInTransaction(state, e);
            log.infof(e, Messages.format("NOCK-014708.chunk.writer.exception.closing", this._context, this.writer.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("NOCK-014709.chunk.writer.throwable.closing", this._context, this.writer.getRef()));
        }
    }

    private void _runBeforeListeners(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("NOCK-014300.chunk.listener.before"), this._context);
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
            _handleException(state, exception);
        }
    }

    private void _runAfterListeners(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("NOCK-014301.chunk.listener.after"), this._context);
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
            _handleException(state, exception);
        }
    }

    private void _runErrorListeners(final State state, final Exception exception) {
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("NOCK-014302.chunk.listener.error"), this._context);
                listener.onError(exception);
            } catch (final Exception e) {
                exception.addSuppressed(e);
            }
        }
    }

    private void _readItem(final State state) throws Exception {
        Exception exception = null;
        try {
            for (final ItemReadListener listener : state.itemReadListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014402.chunk.reader.before"), this._context);
                    listener.beforeRead();
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
            log.debugf(Messages.get("NOCK-014403.chunk.reader.read"), this._context, this.reader.getRef());
            final Object read = state.reader.readItem();
            if (read == null) {
                log.debugf(Messages.get("NOCK-014412.chunk.reader.finished"), this._context);
                state.finished = true;
                state.next(state.objects.isEmpty() ? CHECKPOINT : WRITE);
                return;
            }
            state.stepContext.getMetric(READ_COUNT).increment();
            for (final ItemReadListener listener : state.itemReadListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014404.chunk.reader.after"), this._context);
                    listener.afterRead(read);
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
            state.set(state.processor == null ? ADD : PROCESS, read);
        } catch (final Exception e) {
            log.warnf(Messages.get("NOCK-014405.chunk.reader.error"), this._context, this.reader.getRef(), e.getClass().getCanonicalName());
            for (final ItemReadListener listener : state.itemReadListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014410.chunk.reader.error.listener"), this._context);
                    listener.onReadError(e);
                } catch (final Exception s) {
                    if (exception == null) {
                        exception = s;
                        exception.addSuppressed(e);
                    } else {
                        exception.addSuppressed(s);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            if (getSkippableExceptionClasses().matches(e) && state.isSkipAllowed()) {
                ++state.skipped;
                state.stepContext.getMetric(READ_SKIP_COUNT).increment();
                log.debugf(Messages.get("NOCK-014406.chunk.reader.skip"), this._context, this.reader.getRef(), e.getClass().getCanonicalName());
                for (final SkipReadListener listener : state.skipReadListeners) {
                    try {
                        log.debugf(Messages.get("NOCK-014408.chunk.reader.skip.listener"), this._context);
                        listener.onSkipReadItem(e);
                    } catch (final Exception s) {
                        if (exception == null) {
                            exception = s;
                            exception.addSuppressed(e);
                        } else {
                            exception.addSuppressed(s);
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e) && state.isRetryAllowed()) {
                ++state.retried;
                log.debugf(Messages.get("NOCK-014407.chunk.reader.retry"), this._context, this.reader.getRef(), e.getClass().getCanonicalName());
                for (final RetryReadListener listener : state.retryReadListeners) {
                    try {
                        log.debugf(Messages.get("NOCK-014409.chunk.reader.retry.listener"), this._context);
                        listener.onRetryReadException(e);
                    } catch (final Exception s) {
                        if (exception == null) {
                            exception = s;
                            exception.addSuppressed(e);
                        } else {
                            exception.addSuppressed(s);
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                if (getNoRollbackExceptionClasses().matches(e)) {
                    log.debugf(Messages.get("NOCK-014411.chunk.reader.no.rollback"), this._context, this.reader.getRef(), e.getClass().getCanonicalName());
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
        Exception exception = null;
        try {
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014500.chunk.processor.before"), this._context);
                    listener.beforeProcess(read);
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
            log.debugf(Messages.get("NOCK-014501.chunk.processor.process"), this._context, this.processor.getRef());
            final Object processed = state.processor == null ? read : state.processor.processItem(read);
            if (processed == null) {
                state.stepContext.getMetric(FILTER_COUNT).increment();
                log.debugf(Messages.get("NOCK-014510.chunk.processor.filter"), this._context);
                state.next(state.isReadyToCheckpoint() ? WRITE : READ);
                return;
            }
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014502.chunk.processor.after"), this._context);
                    listener.afterProcess(read, processed);
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
            state.set(ADD, processed);
        } catch (final Exception e) {
            log.warnf(Messages.get("NOCK-014503.chunk.processor.error"), this._context, this.processor.getRef(), e.getClass().getCanonicalName());
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014508.chunk.processor.error.listener"), this._context);
                    listener.onProcessError(read, e);
                } catch (final Exception s) {
                    if (exception == null) {
                        exception = s;
                        exception.addSuppressed(e);
                    } else {
                        exception.addSuppressed(s);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            if (getSkippableExceptionClasses().matches(e) && state.isSkipAllowed()) {
                ++state.skipped;
                state.stepContext.getMetric(PROCESS_SKIP_COUNT).increment();
                log.debugf(Messages.get("NOCK-014504.chunk.processor.skip"), this._context, this.processor.getRef(), e.getClass().getCanonicalName());
                for (final SkipProcessListener listener : state.skipProcessListeners) {
                    try {
                        log.debugf(Messages.get("NOCK-014506.chunk.processor.skip.listener"), this._context);
                        listener.onSkipProcessItem(read, e);
                    } catch (final Exception s) {
                        if (exception == null) {
                            exception = s;
                            exception.addSuppressed(e);
                        } else {
                            exception.addSuppressed(s);
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e) && state.isRetryAllowed()) {
                ++state.retried;
                log.debugf(Messages.get("NOCK-014505.chunk.processor.retry"), this._context, this.processor.getRef(), e.getClass().getCanonicalName());
                for (final RetryProcessListener listener : state.retryProcessListeners) {
                    try {
                        log.debugf(Messages.get("NOCK-014507.chunk.processor.retry.listener"), this._context);
                        listener.onRetryProcessException(read, e);
                    } catch (final Exception s) {
                        if (exception == null) {
                            exception = s;
                            exception.addSuppressed(e);
                        } else {
                            exception.addSuppressed(s);
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                if (getNoRollbackExceptionClasses().matches(e)) {
                    log.debugf(Messages.get("NOCK-014509.chunk.processor.no.rollback"), this._context, this.processor.getRef(), e.getClass().getCanonicalName());
                    state.next(PROCESS);
                    return;
                }
                _rollbackTransaction(state);
                return;
            }
            throw e;
        }
    }

    private void _writeItem(final State state) throws Exception {
        Exception exception = null;
        try {
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014602.chunk.writer.before"), this._context, this.writer.getRef());
                    listener.beforeWrite(state.objects);
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
            log.debugf(Messages.get("NOCK-014603.chunk.writer.write"), this._context, this.writer.getRef());
            state.writer.writeItems(state.objects);
            state.stepContext.getMetric(WRITE_COUNT).increment(state.objects.size());
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014604.chunk.writer.after"), this._context);
                    listener.afterWrite(state.objects);
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
            state.objects.clear();
            state.next(CHECKPOINT);
        } catch (final Exception e) {
            log.warnf(Messages.get("NOCK-014605.chunk.writer.error"), this._context, this.writer.getRef(), e.getClass().getCanonicalName());
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                try {
                    log.debugf(Messages.get("NOCK-014610.chunk.writer.error.listener"), this._context);
                    listener.onWriteError(state.objects, e);
                } catch (final Exception s) {
                    if (exception == null) {
                        exception = s;
                        exception.addSuppressed(e);
                    } else {
                        exception.addSuppressed(s);
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            if (getSkippableExceptionClasses().matches(e) && state.isSkipAllowed()) {
                ++state.skipped;
                state.stepContext.getMetric(WRITE_SKIP_COUNT).increment();
                log.debugf(Messages.get("NOCK-014606.chunk.writer.skip"), this._context, this.writer.getRef(), e.getClass().getCanonicalName());
                for (final SkipWriteListener listener : state.skipWriteListeners) {
                    try {
                        log.debugf(Messages.get("NOCK-014608.chunk.writer.skip.listener"), this._context);
                        listener.onSkipWriteItem(state.objects, e);
                    } catch (final Exception s) {
                        if (exception == null) {
                            exception = s;
                            exception.addSuppressed(e);
                        } else {
                            exception.addSuppressed(s);
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                state.next(COMMIT);
                state.objects.clear();
                return;
            }
            if (getRetryableExceptionClasses().matches(e) && state.isRetryAllowed()) {
                ++state.retried;
                log.debugf(Messages.get("NOCK-014607.chunk.writer.retry"), this._context, this.writer.getRef(), e.getClass().getCanonicalName());
                for (final RetryWriteListener listener : state.retryWriteListeners) {
                    try {
                        log.debugf(Messages.get("NOCK-014609.chunk.writer.retry.listener"), this._context);
                        listener.onRetryWriteException(state.objects, e);
                    } catch (final Exception s) {
                        if (exception == null) {
                            exception = s;
                            exception.addSuppressed(e);
                        } else {
                            exception.addSuppressed(s);
                        }
                    }
                }
                if (exception != null) {
                    throw exception;
                }
                if (getNoRollbackExceptionClasses().matches(e)) {
                    log.debugf(Messages.get("NOCK-014611.chunk.writer.no.rollback"), this._context, this.writer.getRef(), e.getClass().getCanonicalName());
                    state.next(WRITE);
                    return;
                }
                _rollbackTransaction(state);
                return;
            }
            throw e;
        }
    }

    private Exception _checkpoint(final State state, final Executor executor, final ExecutionContext context) throws Exception {
        try {
            log.debugf(Messages.get("NOCK-014900.chunk.reader.checkpoint"), this._context, this.reader.getRef());
            state.readInfo = state.reader.checkpointInfo();
            log.debugf(Messages.get("NOCK-014901.chunk.writer.checkpoint"), this._context, this.writer.getRef());
            state.writeInfo = state.writer.checkpointInfo();
            if (state.partitionId == null) {
                Repository.updateStep(
                        state.repository,
                        state.jobExecutionId,
                        state.stepExecutionId,
                        state.stepContext.getPersistentUserData(),
                        state.stepContext.getMetrics(),
                        new CheckpointImpl(state.readInfo, state.writeInfo)
                );
            } else {
                state.repository.updatePartitionExecution(
                        state.stepExecutionId,
                        state.partitionId,
                        state.stepContext.getPersistentUserData(),
                        state.stepContext.getMetrics(),
                        new CheckpointImpl(state.readInfo, state.writeInfo),
                        new Date()
                );
            }
            _collect(state, executor, context, state.stepContext.getBatchStatus(), state.stepContext.getExitStatus());
            return null;
        } catch (final Exception e) {
            return e;
        }
    }

    private void _commit(final State state, final Exception exception) throws Exception {
        if (exception != null) {
            if (getRetryableExceptionClasses().matches(exception) && state.isRetryAllowed()) {
                ++state.retried;
                log.debugf(Messages.get("NOCK-014902.chunk.checkpoint.retry"), this._context, exception.getClass().getCanonicalName());
                if (getNoRollbackExceptionClasses().matches(exception)) {
                    log.debugf(Messages.get("NOCK-014903.chunk.checkpoint.no.rollback"), this._context, exception.getClass().getCanonicalName());
                    state.next(CHECKPOINT);
                    return;
                }
                _rollbackTransaction(state);
                return;
            }
            throw exception;
        }
        try {
            log.debugf(Messages.get("NOCK-014102.chunk.transaction.commit"), this._context);
            state.transactionManager.commit();
            state.stepContext.getMetric(COMMIT_COUNT).increment();
            state.checkpointAlgorithm.endCheckpoint();
        } catch (final Exception e) {
            if (getRetryableExceptionClasses().matches(e) && state.isRetryAllowed()) {
                ++state.retried;
                log.debugf(Messages.get("NOCK-014902.chunk.checkpoint.retry"), this._context, e.getClass().getCanonicalName());
                if (getNoRollbackExceptionClasses().matches(e)) {
                    log.debugf(Messages.get("NOCK-014903.chunk.checkpoint.no.rollback"), this._context, e.getClass().getCanonicalName());
                    state.next(CHECKPOINT);
                    return;
                }
                _rollbackTransaction(state);
                return;
            }
            throw e;
        }
    }

    private void _rollbackTransaction(final State state) throws Exception {
        state.stepContext.getMetric(ROLLBACK_COUNT).increment();
        try {
            _closeReader(state);
            _closeWriter(state);
        } finally {
            log.debugf(Messages.get("NOCK-014103.chunk.transaction.rollback"), this._context);
            state.transactionManager.rollback();
        }
        log.debugf(Messages.get("NOCK-014101.chunk.transaction.begin"), this._context);
        state.transactionManager.begin();
        _openReader(state);
        _openWriter(state);
        if (!state.isFailed()) {
            log.debugf(Messages.get("NOCK-014102.chunk.transaction.commit"), this._context);
            state.transactionManager.commit();
        } else {
            log.debugf(Messages.get("NOCK-014103.chunk.transaction.rollback"), this._context);
            state.transactionManager.rollback();
        }
        state.objects.clear();
        state.next(BEGIN);
    }

    private void _cleanupTx(final State state) {
        try {
            if (state.isFailed() && state.transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION) {
                log.debugf(Messages.get("NOCK-014103.chunk.transaction.rollback"), this._context);
                state.transactionManager.rollback();
            }
        } catch (final Exception e) {
            _handleException(state, e);
        } catch (final Throwable e) {
            state.setThrowable(e);
        }
    }

    private void _handleException(final State state, final Exception exception) {
        state.setException(exception);
        _runErrorListeners(state, exception);
    }

    private void _handleExceptionInTransaction(final State state, final Exception exception) {
        state.setException(exception);
        try {
            if (state.transactionManager.getStatus() == Status.STATUS_ACTIVE) {
                log.debugf(Messages.get("NOCK-014104.chunk.transaction.rollback.only"), this._context);
                state.transactionManager.setRollbackOnly();
            }
        } catch (final SystemException e) {
            exception.addSuppressed(e);
        }
        _runErrorListeners(state, exception);
    }

    @Override
    public synchronized boolean cancel(final boolean mayInterruptIfRunning) {
        if (this._context != null) {
            final MutableStepContext stepContext = this._context.getStepContext();
            if (stepContext != null) {
                stepContext.setBatchStatus(BatchStatus.STOPPING);
            }
        }
        return super.cancel(mayInterruptIfRunning);
    }

    @Override
    protected String getResolveLogMessage() {
        return Messages.format("NOCK-014800.chunk.resolve", this._context, this);
    }

    @Override
    protected String getRejectLogMessage() {
        return Messages.format("NOCK-014801.chunk.reject", this._context, this);
    }

    @Override
    protected String getCancelLogMessage() {
        return Messages.format("NOCK-014802.chunk.cancel", this._context, this);
    }

    @Override
    protected String getTimeoutExceptionMessage() {
        return Messages.format("NOCK-014004.chunk.timeout", this._context, this);
    }

    private class State {
        int next = BEGIN;
        boolean finished = false;
        Object value;
        Serializable readInfo;
        Serializable writeInfo;
        Exception _exception = null;
        Throwable _failure = null;

        final List<Object> objects;

        final long jobExecutionId;
        final long stepExecutionId;
        final Integer partitionId;
        final TransactionManager transactionManager;
        final ExecutionRepository repository;
        final MutableStepContext stepContext;
        final ExecutionContext context;

        final ArrayList<Item> items;

        final int itemCount;
        final long timeLimit;
        final int skipLimit;
        final int retryLimit;

        int skipped = 0;
        int retried = 0;

        long checkpointStartTime;

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
            this.stepExecutionId = context.getStepExecutionId();
            this.partitionId = context.getPartitionId();
            this.transactionManager = executor.getTransactionManager();
            this.repository = executor.getRepository();
            this.stepContext = context.getStepContext();
            this.context = context;
            this.items = new ArrayList<Item>();

            this.itemCount = Integer.parseInt(ChunkImpl.this.itemCount);
            this.timeLimit = Integer.parseInt(ChunkImpl.this.timeLimit) * 1000L;
            this.skipLimit = ChunkImpl.this.skipLimit == null ? -1 : Integer.parseInt(ChunkImpl.this.skipLimit);
            this.retryLimit = ChunkImpl.this.retryLimit == null ? -1 : Integer.parseInt(ChunkImpl.this.retryLimit);

            if (context.isRestarting()) {
                final Checkpoint checkpoint = this.partitionId == null
                        ? repository.getPreviousStepExecution(this.jobExecutionId, this.stepExecutionId, this.stepContext.getStepName()).getCheckpoint()
                        : repository.getPartitionExecution(this.stepExecutionId, this.partitionId).getCheckpoint();
                this.readInfo = checkpoint == null ? null : checkpoint.getReaderCheckpoint();
                this.writeInfo = checkpoint == null ? null : checkpoint.getWriterCheckpoint();
            } else {
                this.readInfo = null;
                this.writeInfo = null;
            }

            this.objects = ChunkImpl.this.checkpointAlgorithm == null
                    ? new LinkedList<Object>()
                    : new ArrayList<Object>(this.itemCount);

            this.reader = ChunkImpl.this.reader.load(executor, context);
            if (this.reader == null) {
                throw new IllegalStateException(Messages.format("NOCK-014002.chunk.reader.null", ChunkImpl.this._context, ChunkImpl.this.reader.getRef()));
            }
            this.processor = ChunkImpl.this.processor == null
                    ? null
                    : ChunkImpl.this.processor.load(executor, context);
            this.writer = ChunkImpl.this.writer.load(executor, context);
            if (this.writer == null) {
                throw new IllegalStateException(Messages.format("NOCK-014003.chunk.writer.null", ChunkImpl.this._context, ChunkImpl.this.writer.getRef()));
            }
            if (CheckpointPolicy.ITEM.equalsIgnoreCase(ChunkImpl.this.checkpointPolicy) || ChunkImpl.this.checkpointAlgorithm == null) {
                this.checkpointAlgorithm = new ItemCheckpointAlgorithm(timeout, this.itemCount);
            } else {
                this.checkpointAlgorithm = ChunkImpl.this.checkpointAlgorithm.load(executor, context);
            }
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

        public boolean isReadyToCheckpoint() throws Exception {
            return this.checkpointAlgorithm.isReadyToCheckpoint()
                    || (0 != this.timeLimit && this.checkpointStartTime + this.timeLimit < System.currentTimeMillis())
                    || ChunkImpl.this.isCancelled();
        }

        public void next(final int status) {
            this.next = status;
        }

        public void set(final int status, final Object value) {
            this.next = status;
            this.value = value;
        }

        public void setThrowable(final Throwable e) {
            log.warnf(e, Messages.format("NOCK-014701.chunk.throwable", ChunkImpl.this._context));
            if (this._failure == null) {
                this._failure = e;
                this.stepContext.setBatchStatus(BatchStatus.FAILED);
            } else {
                this._failure.addSuppressed(e);
            }
        }

        public void setException(final Exception e) {
            log.warnf(e, Messages.format("NOCK-014700.chunk.exception", ChunkImpl.this._context));
            if (this._exception == null) {
                this._exception = e;
                this.stepContext.setException(e);
                this.stepContext.setBatchStatus(BatchStatus.FAILED);
            } else {
                this._exception.addSuppressed(e);
            }
        }

        public boolean isFailed() {
            return this._exception != null || this._failure != null;
        }

        public boolean isSkipAllowed() {
            return skipLimit == -1 || skipped < skipLimit;
        }

        public boolean isRetryAllowed() {
            return retryLimit == -1 || retried < retryLimit;
        }

        public Exception getFailure() throws Exception {
            if (this._exception != null) {
                if (this._failure != null) {
                    this._exception.addSuppressed(_failure);
                }
                return this._exception;
            }
            if (this._failure != null) {
                return new BatchRuntimeException(Messages.format("NOCK-014001.chunk.failed", ChunkImpl.this._context), this._failure);
            }
            return null;
        }
    }
}
