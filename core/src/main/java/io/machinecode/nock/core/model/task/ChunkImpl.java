package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.ChunkFactory;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.spi.Checkpoint;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
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
import javax.batch.runtime.context.StepContext;
import javax.transaction.Status;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkImpl extends DeferredImpl<Void> implements Chunk, TaskWork {

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

    @Override
    public String element() {
        return ELEMENT;
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

        final int itemCount;
        final int timeLimit;
        final int skipLimit;
        final int retryLimit;

        final TransactionManager transactionManager;

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

        private State(final Transport transport, final Context context, final int timeout,
                      final TransactionManager transactionManager, final Checkpoint checkpoint) throws Exception {
            this.jobExecutionId = context.getJobExecutionId();
            this.transactionManager = transactionManager;

            this.itemCount = Integer.parseInt(ChunkImpl.this.itemCount);
            this.timeLimit = Integer.parseInt(ChunkImpl.this.timeLimit);
            this.skipLimit = Integer.parseInt(ChunkImpl.this.skipLimit);
            this.retryLimit = Integer.parseInt(ChunkImpl.this.retryLimit);

            if (checkpoint != null) {
                this.readInfo = checkpoint.getReaderCheckpoint();
                this.writeInfo = checkpoint.getWriterCheckpoint();
            }

            this.objects = ChunkImpl.this.checkpointAlgorithm == null
                    ? new LinkedList<Object>()
                    : new ArrayList<Object>(this.itemCount);

            this.reader = ChunkImpl.this.reader.load(transport, context);
            if (this.reader == null) {
                throw new IllegalStateException(Message.format("chunk.reader.null", jobExecutionId, ChunkImpl.this.reader.getRef()));
            }
            this.processor = ChunkImpl.this.processor == null
                    ? null
                    : ChunkImpl.this.processor.load(transport, context);
            this.writer = ChunkImpl.this.writer.load(transport, context);
            if (this.writer == null) {
                throw new IllegalStateException(Message.format("chunk.writer.null", jobExecutionId, ChunkImpl.this.writer.getRef()));
            }
            this.checkpointAlgorithm = CheckpointPolicy.ITEM.equalsIgnoreCase(ChunkImpl.this.checkpointPolicy) || ChunkImpl.this.checkpointAlgorithm == null //TODO Check this is caught in the job validator and remove this
                    ? new ItemCheckpointAlgorithm(timeout, this.itemCount)
                    : ChunkImpl.this.checkpointAlgorithm.load(transport, context);
            this.chunkListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, ChunkListener.class);
            this.itemReadListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, ItemReadListener.class);
            this.retryReadListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, RetryReadListener.class);
            this.skipReadListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, SkipReadListener.class);
            this.itemProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, ItemProcessListener.class);
            this.retryProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, RetryProcessListener.class);
            this.skipProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, SkipProcessListener.class);
            this.itemWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, ItemWriteListener.class);
            this.retryWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, RetryWriteListener.class);
            this.skipWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(transport, context, SkipWriteListener.class);
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

        public void tryThrow(final String message) throws Exception {
            if (this.exception != null) {
                if (this.failure != null) {
                    this.exception.addSuppressed(failure);
                }
                throw this.exception;
            }
            if (this.failure != null) {
                throw new BatchRuntimeException(Message.format(message, this.jobExecutionId), this.failure);
            }
        }
    }

    @Override
    public TaskWork partition(final PropertyContext context) {
        return ChunkFactory.INSTANCE.producePartitioned(this, this.listeners, this.partition, context);
    }

    @Override
    public void run(final Transport transport, final Context context, final int timeout) throws Exception {
        final ExecutionRepository repository = transport.getRepository();
        final StepContext stepContext = context.getStepContext();
        final State state = new State(
                transport,
                context,
                timeout,
                transport.getTransactionManager(),
                repository.getStepExecutionCheckpoint(stepContext.getStepExecutionId())
        );

        log.debugf(Message.get("chunk.transaction.timeout"), state.jobExecutionId, timeout);
        state.transactionManager.setTransactionTimeout(timeout);
        state.transactionManager.begin();
        openReader(state);
        openWriter(state);
        try {
            if (!state.failed()) {
                state.transactionManager.commit();
            } else {
                state.transactionManager.setRollbackOnly();
                closeReader(state);
                closeWriter(state);
                state.transactionManager.rollback();
            }
        } catch (final Throwable e) {
            state.throwable(e);
        }
        state.tryThrow("chunk.failed.opening");

        try {
            try {
                while (!state.failed() && loop(state, transport, context, repository, stepContext)) {
                    //
                }
            } catch (final Exception e) {
                state.exception(e);
                state.transactionManager.setRollbackOnly();
                error(state, e);
                log.infof(e, Message.format("chunk.exception", state.jobExecutionId));
            } catch (final Throwable e) {
                state.throwable(e);
                state.transactionManager.setRollbackOnly();
                log.infof(e, Message.format("chunk.throwable", state.jobExecutionId));
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
        closeReader(state);
        closeWriter(state);
        if (!state.failed()) {
            collect(state, transport, context);
            state.transactionManager.commit();
        } else {
            state.transactionManager.rollback();
        }
        state.tryThrow("chunk.failed");
    }

    private boolean loop(final State state, final Transport transport, final Context context, final ExecutionRepository repository, final StepContext stepContext) throws Exception {
        if (BatchStatus.STOPPING.equals(stepContext.getBatchStatus())) {
            cancel(true);
        }
        switch (state.next) {
            case BEGIN:
                final int checkpointTimeout = state.checkpointAlgorithm.checkpointTimeout();
                log.debugf(Message.get("chunk.transaction.timeout"), state.jobExecutionId, checkpointTimeout);
                state.transactionManager.setTransactionTimeout(checkpointTimeout);
                log.debugf(Message.get("chunk.state.begin"), state.jobExecutionId);
                state.checkpointAlgorithm.beginCheckpoint();
                state.transactionManager.begin();
                before(state);
                state.next(readyToCheckpoint(state) ? WRITE : READ);
                break;
            case READ:
                log.debugf(Message.get("chunk.state.read"), state.jobExecutionId);
                read(state);
                break;
            case PROCESS:
                log.debugf(Message.get("chunk.state.process"), state.jobExecutionId);
                process(state.value, state);
                break;
            case ADD:
                log.debugf(Message.get("chunk.state.add"), state.jobExecutionId);
                state.objects.add(state.value);
                state.next(readyToCheckpoint(state) ? WRITE : READ);
                break;
            case WRITE:
                log.debugf(Message.get("chunk.state.write"), state.jobExecutionId);
                write(state);
                // 11.8 9 m has this outside the write catch block
                after(state);
                break;
            case COMMIT:
                log.debugf(Message.get("chunk.state.commit"), state.jobExecutionId);
                log.debugf(Message.get("chunk.reader.checkpoint"), state.jobExecutionId, reader.getRef());
                state.readInfo = state.reader.checkpointInfo();
                log.debugf(Message.get("chunk.writer.checkpoint"), state.jobExecutionId, writer.getRef());
                state.writeInfo = state.writer.checkpointInfo();

                repository.updateStepExecution(
                        stepContext.getStepExecutionId(),
                        stepContext.getPersistentUserData(),
                        new CheckpointImpl(state.readInfo, state.writeInfo),
                        new Date()
                );
                state.transactionManager.commit();
                state.checkpointAlgorithm.endCheckpoint();
                collect(state, transport, context);
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

    private boolean readyToCheckpoint(final State state) throws Exception {
        return state.checkpointAlgorithm.isReadyToCheckpoint() || isCancelled();
    }

    private void collect(final State state, final Transport transport, final Context context) {
        try {
            if (partition != null) {
                partition.collect(this, transport, context);
            }
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Message.format("chunk.partition.exception.collect", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Message.format("chunk.partition.throwable.collect", state.jobExecutionId));
        }
    }

    private void openReader(final State state) {
        try {
            //TODO Read things
            log.debugf(Message.get("chunk.reader.open"), state.jobExecutionId, reader.getRef());
            state.reader.open(state.readInfo);
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Message.format("chunk.reader.exception.opening", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Message.format("chunk.reader.throwable.opening", state.jobExecutionId));
        }
    }

    private void closeReader(final State state) {
        try {
            log.debugf(Message.get("chunk.reader.close"), state.jobExecutionId, reader.getRef());
            state.reader.close();
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Message.format("chunk.reader.exception.closing", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Message.format("chunk.reader.throwable.closing", state.jobExecutionId));
        }
    }

    private void openWriter(final State state) {
        try {
            //TODO Read things
            log.debugf(Message.get("chunk.writer.open"), state.jobExecutionId, reader.getRef());
            state.writer.open(state.writeInfo);
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Message.format("chunk.writer.exception.opening", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Message.format("chunk.writer.throwable.opening", state.jobExecutionId));
        }
    }

    private void closeWriter(final State state) {
        try {
            log.debugf(Message.get("chunk.writer.close"), state.jobExecutionId, writer.getRef());
            state.writer.close();
        } catch (final Exception e) {
            state.exception(e);
            log.infof(e, Message.format("chunk.writer.exception.closing", state.jobExecutionId));
        } catch (final Throwable e) {
            state.throwable(e);
            log.infof(e, Message.format("chunk.writer.throwable.closing", state.jobExecutionId));
        }
    }

    private void before(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Message.get("chunk.listener.before"), state.jobExecutionId);
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

    private void after(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Message.get("chunk.listener.after"), state.jobExecutionId);
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

    private void error(final State state, final Exception that) throws Exception {
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                log.debugf(Message.get("chunk.listener.error"), state.jobExecutionId);
                listener.onError(that);
            } catch (final Exception e) {
                state.exception(e);
            }
        }
    }

    private void read(final State state) throws Exception {
        try {
            for (final ItemReadListener listener : state.itemReadListeners) {
                log.debugf(Message.get("chunk.reader.before"), state.jobExecutionId);
                listener.beforeRead();
            }
            log.debugf(Message.get("chunk.reader.read"), state.jobExecutionId, reader.getRef());
            final Object read = state.reader.readItem();
            if (read == null) {
                state.finished = true;
                state.next(WRITE);
                return;
            }
            for (final ItemReadListener listener : state.itemReadListeners) {
                log.debugf(Message.get("chunk.reader.after"), state.jobExecutionId);
                listener.afterRead(read);
            }
            state.set(state.processor == null ? ADD : PROCESS, read);
        } catch (final Exception e) {
            for (final ItemReadListener listener : state.itemReadListeners) {
                log.debugf(Message.get("chunk.reader.error"), state.jobExecutionId);
                listener.onReadError(e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                for (final SkipReadListener listener : state.skipReadListeners) {
                    log.debugf(Message.get("chunk.reader.skip"), state.jobExecutionId);
                    listener.onSkipReadItem(e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryReadListener listener : state.retryReadListeners) {
                    log.debugf(Message.get("chunk.reader.retry"), state.jobExecutionId);
                    listener.onRetryReadException(e);
                }
                if (getNoRollbackExceptionClasses().matches(e)) {
                    state.next(READ);
                    return;
                }
                rollback(state);
                return;
            }
            throw e;
        }
    }

    private void process(final Object read, final State state) throws Exception {
        try {
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                log.debugf(Message.get("chunk.processor.before"), state.jobExecutionId);
                listener.beforeProcess(read);
            }
            log.debugf(Message.get("chunk.processor.process"), state.jobExecutionId, processor.getRef());
            final Object processed = state.processor == null ? read : state.processor.processItem(read);
            if (processed == null) {
                state.next(readyToCheckpoint(state) ? WRITE : READ);
                return;
            }
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                log.debugf(Message.get("chunk.processor.after"), state.jobExecutionId);
                listener.afterProcess(read, processed);
            }
            state.set(ADD, processed);
        } catch (final Exception e) {
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                log.debugf(Message.get("chunk.processor.error"), state.jobExecutionId);
                listener.onProcessError(read, e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                for (final SkipProcessListener listener : state.skipProcessListeners) {
                    log.debugf(Message.get("chunk.processor.skip"), state.jobExecutionId);
                    listener.onSkipProcessItem(read, e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryProcessListener listener : state.retryProcessListeners) {
                    log.debugf(Message.get("chunk.processor.retry"), state.jobExecutionId);
                    listener.onRetryProcessException(read, e);
                }
                rollback(state);
                return;
            }
            throw e;
        }
    }

    private void write(final State state) throws Exception {
        try {
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                log.debugf(Message.get("chunk.writer.before"), state.jobExecutionId);
                listener.beforeWrite(state.objects);
            }
            log.debugf(Message.get("chunk.writer.write"), state.jobExecutionId, writer.getRef());
            state.writer.writeItems(state.objects);
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                log.debugf(Message.get("chunk.writer.after"), state.jobExecutionId);
                listener.afterWrite(state.objects);
            }
            state.next(COMMIT);
        } catch (final Exception e) {
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                log.debugf(Message.get("chunk.writer.error"), state.jobExecutionId);
                listener.onWriteError(state.objects, e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                for (final SkipWriteListener listener : state.skipWriteListeners) {
                    log.debugf(Message.get("chunk.writer.skip"), state.jobExecutionId);
                    listener.onSkipWriteItem(state.objects, e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryWriteListener listener : state.retryWriteListeners) {
                    log.debugf(Message.get("chunk.writer.retry"), state.jobExecutionId);
                    listener.onRetryWriteException(state.objects, e);
                }
                rollback(state);
                return;
            }
            throw e;
        } finally {
            state.objects.clear();
        }
    }

    private void rollback(final State state) throws Exception {
        try {
            closeReader(state);
            closeWriter(state);
        } finally {
            state.transactionManager.rollback();
        }

        state.transactionManager.begin();
            openReader(state);
            openWriter(state);
        if (!state.failed()) {
            state.transactionManager.commit();
        } else {
            state.transactionManager.rollback();
        }
        state.objects.clear();
        state.next(BEGIN);
    }

    public static final class CheckpointImpl implements Checkpoint {

        private final Serializable reader;
        private final Serializable writer;

        public CheckpointImpl(final Serializable reader, final Serializable writer) {
            this.reader = reader;
            this.writer = writer;
        }

        @Override
        public Serializable getReaderCheckpoint() {
            return reader;
        }

        @Override
        public Serializable getWriterCheckpoint() {
            return writer;
        }
    }

    private static final class ItemCheckpointAlgorithm implements CheckpointAlgorithm {

        final int timeout;
        final int target;
        int current;

        public ItemCheckpointAlgorithm(final int timeout, final int target) {
            this.timeout = timeout;
            this.target = target;
        }

        @Override
        public int checkpointTimeout() throws Exception {
            return timeout;
        }

        @Override
        public void beginCheckpoint() throws Exception {
            current = 0;
        }

        @Override
        public boolean isReadyToCheckpoint() throws Exception {
            return target == current++;
        }

        @Override
        public void endCheckpoint() throws Exception {
            //
        }
    }
}
