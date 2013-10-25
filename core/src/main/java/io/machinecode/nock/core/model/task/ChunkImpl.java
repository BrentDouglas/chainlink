package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.factory.task.ChunkFactory;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.model.partition.PartitionImpl;
import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;
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
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.StepContext;
import javax.transaction.TransactionManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
        Object value;
        Serializable readInfo = null;
        Serializable writeInfo = null;

        final List<Object> objects;

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

        private State(final InjectionContext injectionContext, final TransactionManager transactionManager) throws Exception {
            this.transactionManager = transactionManager;

            this.itemCount = Integer.parseInt(ChunkImpl.this.itemCount);
            this.timeLimit = Integer.parseInt(ChunkImpl.this.timeLimit);
            this.skipLimit = Integer.parseInt(ChunkImpl.this.skipLimit);
            this.retryLimit = Integer.parseInt(ChunkImpl.this.retryLimit);

            this.objects = new ArrayList<Object>(this.itemCount);

            this.reader = ChunkImpl.this.reader.load(injectionContext);
            this.processor = ChunkImpl.this.processor == null ? null : ChunkImpl.this.processor.load(injectionContext);
            this.writer = ChunkImpl.this.writer.load(injectionContext);
            this.checkpointAlgorithm = ChunkImpl.this.checkpointAlgorithm == null ? null : ChunkImpl.this.checkpointAlgorithm.load(injectionContext);
            this.chunkListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, ChunkListener.class);
            this.itemReadListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, ItemReadListener.class);
            this.retryReadListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, RetryReadListener.class);
            this.skipReadListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, SkipReadListener.class);
            this.itemProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, ItemProcessListener.class);
            this.retryProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, RetryProcessListener.class);
            this.skipProcessListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, SkipProcessListener.class);
            this.itemWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, ItemWriteListener.class);
            this.retryWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, RetryWriteListener.class);
            this.skipWriteListeners = ChunkImpl.this.listeners.getListenersImplementing(injectionContext, SkipWriteListener.class);

            if (this.reader == null) {
                throw new IllegalStateException(ChunkImpl.this.reader.getRef());
            }
            if (this.writer == null) {
                throw new IllegalStateException(ChunkImpl.this.writer.getRef());
            }
        }

        public void next(final int status) {
            this.next = status;
        }

        public void set(final int status, final Object value) {
            this.next = status;
            this.value = value;
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
                transport.createInjectionContext(context),
                transport.getTransactionManager()
        );

        state.transactionManager.setTransactionTimeout(timeout);
        state.transactionManager.begin();
        try {
            state.reader.open(null); //TODO Read things
            state.writer.open(null);
            state.transactionManager.commit();
        } catch (final Exception e) {
            state.transactionManager.rollback();
            context.getStepContext().setExitStatus(BatchStatus.FAILED.name());
            throw e;
        }

        try {
            outer: for (;;) {
                if (BatchStatus.STOPPING.equals(stepContext.getBatchStatus())) {
                    cancel(true);
                }
                switch (state.next) {
                    case BEGIN:
                        state.checkpointAlgorithm.beginCheckpoint();
                        state.transactionManager.begin();
                        state.transactionManager.setTransactionTimeout(state.checkpointAlgorithm.checkpointTimeout());
                        before(state);
                        state.next(state.checkpointAlgorithm.isReadyToCheckpoint() || isCancelled() ? WRITE : READ);
                        break;
                    case READ:
                        read(state);
                        break;
                    case PROCESS:
                        process(state.value, state);
                        break;
                    case ADD:
                        state.objects.add(state.value);
                        state.next(state.checkpointAlgorithm.isReadyToCheckpoint() || isCancelled() ? WRITE : READ);
                        break;
                    case WRITE:
                        write(state);
                        // 11.8 9 m has this outside the write catch block
                        after(state);
                        break;
                    case COMMIT:
                        state.readInfo = state.reader.checkpointInfo();
                        state.writeInfo = state.writer.checkpointInfo();

                        repository.updateStepExecution(
                                stepContext.getStepExecutionId(),
                                stepContext.getPersistentUserData(),
                                new Date()
                        );
                        state.transactionManager.commit();
                        if (partition != null) {
                            partition.collect(this, transport, context);
                        }
                        if (isCancelled()) {
                            break outer;
                        }
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
        } catch (final Exception e) {
            log.debugf(e, "Chunk threw "); //TODO Message
            state.transactionManager.setRollbackOnly();
            error(state, e);
            state.transactionManager.rollback();
        }
        state.transactionManager.begin();
        try {
            state.reader.close(); //TODO Read things
            state.writer.close();
            if (partition != null) {
                partition.collect(this, transport, context);
            }
            state.transactionManager.commit();
        } catch (final Exception e) {
            state.transactionManager.rollback();
        }
    }

    private void before(final State state) throws Exception {
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
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
        Exception exception = null;
        for (final ChunkListener listener : state.chunkListeners) {
            try {
                listener.onError(that);
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

    private void read(final State state) throws Exception {
        try {
            for (final ItemReadListener listener : state.itemReadListeners) {
                listener.beforeRead();
            }
            final Object read = state.reader.readItem();
            for (final ItemReadListener listener : state.itemReadListeners) {
                listener.afterRead(read);
            }
            state.set(PROCESS, read);
        } catch (final Exception e) {
            for (final ItemReadListener listener : state.itemReadListeners) {
                listener.onReadError(e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                for (final SkipReadListener listener : state.skipReadListeners) {
                    listener.onSkipReadItem(e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryReadListener listener : state.retryReadListeners) {
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
                listener.beforeProcess(read);
            }
            final Object processed = state.processor == null ? read : state.processor.processItem(read);
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                listener.afterProcess(read, processed);
            }
            state.set(ADD, processed);
        } catch (final Exception e) {
            for (final ItemProcessListener listener : state.itemProcessListeners) {
                listener.onProcessError(read, e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                for (final SkipProcessListener listener : state.skipProcessListeners) {
                    listener.onSkipProcessItem(read, e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryProcessListener listener : state.retryProcessListeners) {
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
                listener.beforeWrite(state.objects);
            }
            state.writer.writeItems(state.objects);
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                listener.afterWrite(state.objects);
            }
            state.next(COMMIT);
        } catch (final Exception e) {
            for (final ItemWriteListener listener : state.itemWriteListeners) {
                listener.onWriteError(state.objects, e);
            }
            if (getSkippableExceptionClasses().matches(e)) {
                for (final SkipWriteListener listener : state.skipWriteListeners) {
                    listener.onSkipWriteItem(state.objects, e);
                }
                state.next(READ);
                return;
            }
            if (getRetryableExceptionClasses().matches(e)) {
                for (final RetryWriteListener listener : state.retryWriteListeners) {
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
            state.writer.close();
            state.reader.close();
        } finally {
            state.transactionManager.rollback();
        }

        state.transactionManager.begin();
        try {
            state.writer.open(state.writeInfo);
            state.reader.open(state.readInfo);
            state.transactionManager.commit();
        } catch (final Exception e) {
            state.transactionManager.rollback();
        }
        state.objects.clear();
        state.next(BEGIN);
    }
}
