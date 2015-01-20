package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.context.ItemImpl;
import io.machinecode.chainlink.core.element.ListenerImpl;
import io.machinecode.chainlink.core.element.ListenersImpl;
import io.machinecode.chainlink.core.element.partition.PartitionImpl;
import io.machinecode.chainlink.core.factory.task.ChunkFactory;
import io.machinecode.chainlink.core.util.Repository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.Item;
import io.machinecode.chainlink.spi.context.MutableStepContext;
import io.machinecode.chainlink.spi.element.task.Chunk;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.BaseExecution;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.TaskWork;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.api.chunk.CheckpointAlgorithm;
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
import java.util.concurrent.TimeUnit;

import static javax.batch.runtime.Metric.MetricType.COMMIT_COUNT;
import static javax.batch.runtime.Metric.MetricType.FILTER_COUNT;
import static javax.batch.runtime.Metric.MetricType.PROCESS_SKIP_COUNT;
import static javax.batch.runtime.Metric.MetricType.READ_COUNT;
import static javax.batch.runtime.Metric.MetricType.READ_SKIP_COUNT;
import static javax.batch.runtime.Metric.MetricType.ROLLBACK_COUNT;
import static javax.batch.runtime.Metric.MetricType.WRITE_COUNT;
import static javax.batch.runtime.Metric.MetricType.WRITE_SKIP_COUNT;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChunkImpl implements Chunk, TaskWork, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ChunkImpl.class);

    private static final int BEGIN = 1;
    private static final int READ = 2;
    private static final int PROCESS = 3;
    private static final int ADD = 4;
    private static final int WRITE = 5;
    private static final int AFTER = 6;
    private static final int READER_CHECKPOINT = 7;
    private static final int WRITER_CHECKPOINT = 8;
    private static final int COLLECT = 9;
    private static final int COMMIT = 10;

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
    public TaskWork partition(final PropertyContext context) {
        return ChunkFactory.INSTANCE.producePartitioned(this, this.listeners, this.partition, context);
    }

    @Override
    public void run(final Configuration configuration, final Promise<?,Throwable,?> promise, final ExecutionRepositoryId executionRepositoryId,
                    final ExecutionContext context, final int timeout) throws Throwable {
        final Long partitionExecutionId = context.getPartitionExecutionId();
        final MutableStepContext stepContext = context.getStepContext();
        final State state;
        try {
            state = new State(
                    this,
                    configuration,
                    promise,
                    executionRepositoryId,
                    context,
                    timeout
            );
        } catch (final Throwable e) {
            if (partitionExecutionId != null) {
                Repository.getExecutionRepository(configuration, executionRepositoryId).finishPartitionExecution(
                        partitionExecutionId,
                        stepContext.getMetrics(),
                        stepContext.getPersistentUserData(),
                        BatchStatus.FAILED,
                        stepContext.getExitStatus(),
                        new Date()
                );
            }
            throw e;
        }
        try {
            state.stepContext.setBatchStatus(BatchStatus.STARTED);
            if (partitionExecutionId != null) {
                state.repository.startPartitionExecution(
                        partitionExecutionId,
                        new Date()
                );
            }

            try {
                log.debugf(Messages.get("CHAINLINK-014100.chunk.transaction.timeout"), state.context, timeout);
                state.transactionManager.setTransactionTimeout(timeout);
                log.debugf(Messages.get("CHAINLINK-014101.chunk.transaction.begin"), state.context);
                state.transactionManager.begin();
                _openReader(state);
                if (state.isFailed()) {
                    _closeReader(state);
                    log.debugf(Messages.get("CHAINLINK-014103.chunk.transaction.rollback"), state.context);
                    _runErrorListeners(state, state._exception);
                    state.transactionManager.rollback();
                } else {
                    _openWriter(state);
                    if (!state.isFailed()) {
                        log.debugf(Messages.get("CHAINLINK-014102.chunk.transaction.commit"), state.context);
                        state.transactionManager.commit();
                    } else {
                        _closeWriter(state);
                        _closeReader(state);
                        log.debugf(Messages.get("CHAINLINK-014103.chunk.transaction.rollback"), state.context);
                        _runErrorListeners(state, state._exception);
                        state.transactionManager.rollback();
                    }
                }
            } catch (final Exception e) {
                _handleException(state, e);
            } catch (final Throwable e) {
                state.setThrowable(e);
            } finally {
                _cleanupTx(state);
            }

            if (state.isFailed()) {
                if (partitionExecutionId != null) {
                    state.repository.finishPartitionExecution(
                            partitionExecutionId,
                            stepContext.getMetrics(),
                            stepContext.getPersistentUserData(),
                            BatchStatus.FAILED,
                            stepContext.getExitStatus(),
                            new Date()
                    );
                }
                throw state.getFailure();
            }

            try {
                while (_loop(state)) {
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
                log.debugf(Messages.get("CHAINLINK-014101.chunk.transaction.begin"), state.context);
                final Exception e = state.takeException();
                final Throwable f = state.takeFailure();
                try {
                    state.transactionManager.begin();
                    _closeWriter(state);
                    _closeReader(state);
                    if (!state.isFailed()) {
                        log.debugf(Messages.get("CHAINLINK-014102.chunk.transaction.commit"), state.context);
                        state.transactionManager.commit();
                        //_collect(state, context, state.stepContext.getBatchStatus(), state.stepContext.getExitStatus());
                    } else {
                        log.debugf(Messages.get("CHAINLINK-014103.chunk.transaction.rollback"), state.context);
                        _runErrorListeners(state, state._exception);
                        state.transactionManager.rollback();
                    }
                } finally {
                    state.give(e, f);
                }
            } catch (final Exception e) {
                _handleException(state, e);
            } catch (final Throwable e) {
                state.setThrowable(e);
            } finally {
                context.setItems(state.items.toArray(new Item[state.items.size()]));
                _cleanupTx(state);
            }
            if (state.isFailed()) {
                throw state.getFailure();
            }
        } finally {
            final BatchStatus batchStatus;
            if (promise.isCancelled()) {
                state.stepContext.setBatchStatus(batchStatus = BatchStatus.STOPPING);
            } else if (state.isFailed()) {
                batchStatus = BatchStatus.FAILED;
            } else {
                batchStatus = BatchStatus.COMPLETED;
            }
            if (partitionExecutionId != null) {
                state.repository.finishPartitionExecution(
                        partitionExecutionId,
                        stepContext.getMetrics(),
                        stepContext.getPersistentUserData(),
                        batchStatus,
                        stepContext.getExitStatus(),
                        new Date()
                );
            }
        }
    }

    private boolean _loop(final State state) throws Exception {
        if (state.isFailed()) {
            return false;
        }
        switch (state.next) {
            case BEGIN:
                log.debugf(Messages.get("CHAINLINK-014200.chunk.state.begin"), state.context);
                final int checkpointTimeout = state.checkpointTimeout();
                log.debugf(Messages.get("CHAINLINK-014100.chunk.transaction.timeout"), state.context, checkpointTimeout);
                state.transactionManager.setTransactionTimeout(checkpointTimeout);
                state.checkpointStartTime = System.currentTimeMillis();
                state.beginCheckpoint();
                log.debugf(Messages.get("CHAINLINK-014101.chunk.transaction.begin"), state.context);
                state.transactionManager.begin();
                _runBeforeListeners(state);
                state.next(READ);
                break;
            case READ:
                log.debugf(Messages.get("CHAINLINK-014201.chunk.state.read"), state.context);
                _readItem(state);
                break;
            case PROCESS:
                log.debugf(Messages.get("CHAINLINK-014202.chunk.state.process"), state.context);
                _processItem(state.value, state);
                break;
            case ADD:
                log.debugf(Messages.get("CHAINLINK-014203.chunk.state.add"), state.context);
                state.objects.add(state.value);
                state.next(state.isReadyToCheckpoint() ? WRITE : READ);
                break;
            case WRITE:
                log.debugf(Messages.get("CHAINLINK-014204.chunk.state.write"), state.context);
                _writeItem(state);
                break;
            case AFTER:
                log.debugf(Messages.get("CHAINLINK-014205.chunk.state.after"), state.context);
                // 11.8 9 m has this outside the write catch block
                _runAfterListeners(state);
                state.objects.clear();
                state.next(READER_CHECKPOINT);
                break;
            case READER_CHECKPOINT:
                if (state.isFailed()) {
                    return false;
                }
                state.next(_readerCheckpoint(state));
                break;
            case WRITER_CHECKPOINT:
                if (state.isFailed()) {
                    return false;
                }
                state.next(_writerCheckpoint(state));
                break;
            case COLLECT:
                if (state.isFailed()) {
                    return false;
                }
                _collect(state, state.context, state.stepContext.getBatchStatus(), state.stepContext.getExitStatus());
                state.next(COMMIT);
                break;
            case COMMIT:
                _commit(state);
                if (state.promise.isCancelled()) {
                    return false;
                }
                if (state.finished) {
                    return false;
                }
                state.next(BEGIN);
                break;
            default:
                throw new IllegalStateException(Messages.format("CHAINLINK-014000.chunk.illegal.state", state.context, state.next));
        }
        return true;
    }

    private void _collect(final State state, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        // TODO #checkpointInfo failed maybe it would be better to ignore updating that part
        if (state.partitionExecutionId == null) {
            Repository.updateStep(
                    state.repository,
                    state.jobExecutionId,
                    state.stepExecutionId,
                    state.stepContext.getMetrics(),
                    state.stepContext.getPersistentUserData(),
                    state.readInfo,
                    state.writeInfo
            );
        } else {
            state.repository.updatePartitionExecution(
                    state.partitionExecutionId,
                    state.stepContext.getMetrics(),
                    state.stepContext.getPersistentUserData(),
                    state.readInfo,
                    state.writeInfo,
                    new Date()
            );
        }
        try {
            if (partition != null) {
                state.items.add(partition.collect(state.configuration, context, batchStatus, exitStatus));
            } else {
                state.items.add(new ItemImpl(null, batchStatus, exitStatus));
            }
        } catch (final Exception e) {
            log.warnf(Messages.format("CHAINLINK-014710.chunk.partition.exception.collect", state.context, e.getClass().getCanonicalName()));
            _handleException(state, e);
        } catch (final Throwable e) {
            log.warnf(Messages.format("CHAINLINK-014711.chunk.partition.throwable.collect", state.context, e.getClass().getCanonicalName()));
            state.setThrowable(e);
        }
    }

    private void _openReader(final State state) {
        try {
            log.debugf(Messages.get("CHAINLINK-014400.chunk.reader.open"), state.context, this.reader.getRef());
            this.reader.open(state.configuration, state.context, state.readInfo);
        } catch (final Exception e) {
            _handleException(state, e);
            log.infof(e, Messages.format("CHAINLINK-014702.chunk.reader.exception.opening", state.context, this.reader.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("CHAINLINK-014703.chunk.reader.throwable.opening", state.context, this.reader.getRef()));
        }
    }

    private void _closeReader(final State state) {
        try {
            log.debugf(Messages.get("CHAINLINK-014401.chunk.reader.close"), state.context, this.reader.getRef());
            this.reader.close(state.configuration, state.context);
        } catch (final Exception e) {
            _handleException(state, e);
            log.infof(e, Messages.format("CHAINLINK-014706.chunk.reader.exception.closing", state.context, this.reader.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("CHAINLINK-014707.chunk.reader.throwable.closing", state.context, this.reader.getRef()));
        }
    }

    private void _openWriter(final State state) {
        try {
            log.debugf(Messages.get("CHAINLINK-014600.chunk.writer.open"), state.context, this.writer.getRef());
            this.writer.open(state.configuration, state.context, state.writeInfo);
        } catch (final Exception e) {
            _handleException(state, e);
            log.infof(e, Messages.format("CHAINLINK-014704.chunk.writer.exception.opening", state.context, this.writer.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("CHAINLINK-014705.chunk.writer.throwable.opening", state.context, this.writer.getRef()));
        }
    }

    private void _closeWriter(final State state) {
        try {
            log.debugf(Messages.get("CHAINLINK-014601.chunk.writer.close"), state.context, this.writer.getRef());
            this.writer.close(state.configuration, state.context);
        } catch (final Exception e) {
            _handleException(state, e);
            log.infof(e, Messages.format("CHAINLINK-014708.chunk.writer.exception.closing", state.context, this.writer.getRef()));
        } catch (final Throwable e) {
            state.setThrowable(e);
            log.infof(e, Messages.format("CHAINLINK-014709.chunk.writer.throwable.closing", state.context, this.writer.getRef()));
        }
    }

    private void _runBeforeListeners(final State state) throws Exception {
        Exception exception = null;
        for (final ListenerImpl listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("CHAINLINK-014300.chunk.listener.before"), state.context);
                listener.beforeChunk(state.configuration, state.context);
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
        for (final ListenerImpl listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("CHAINLINK-014301.chunk.listener.after"), state.context);
                listener.afterChunk(state.configuration, state.context);
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
        if (exception == null) {
            // This means the rollback was triggered by a Throwable which we can't pass
            return;
        }
        for (final ListenerImpl listener : state.chunkListeners) {
            try {
                log.debugf(Messages.get("CHAINLINK-014302.chunk.listener.error"), state.context);
                listener.onError(state.configuration, state.context, exception);
            } catch (final Exception e) {
                exception.addSuppressed(e);
            }
        }
    }

    private void _readItem(final State state) throws Exception {
        Exception exception = null;
        try {
            for (final ListenerImpl listener : state.itemReadListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014402.chunk.reader.before"), state.context);
                    listener.beforeRead(state.configuration, state.context);
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
            log.debugf(Messages.get("CHAINLINK-014403.chunk.reader.read"), state.context, this.reader.getRef());
            final Object read = this.reader.readItem(state.configuration, state.context);
            for (final ListenerImpl listener : state.itemReadListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014404.chunk.reader.after"), state.context);
                    listener.afterRead(state.configuration, state.context, read);
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            if (read == null) {
                log.debugf(Messages.get("CHAINLINK-014412.chunk.reader.finished"), state.context);
                state.finished = true;
                state.next(state.objects.isEmpty() ? AFTER : WRITE);
                return;
            }
            state.stepContext.getMetric(READ_COUNT).increment();
            if (exception != null) {
                throw exception;
            }
            state.set(this.processor == null ? ADD : PROCESS, read);
        } catch (final Exception e) {
            log.warnf(Messages.get("CHAINLINK-014405.chunk.reader.error"), state.context, this.reader.getRef(), e.getClass().getCanonicalName());
            for (final ListenerImpl listener : state.itemReadListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014410.chunk.reader.error.listener"), state.context);
                    listener.onReadError(state.configuration, state.context, e);
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
            final Match match = _tryMatchException(state, e);
            switch (match) {
                case SKIP:
                    ++state.skipped;
                    state.stepContext.getMetric(READ_SKIP_COUNT).increment();
                    log.debugf(Messages.get("CHAINLINK-014406.chunk.reader.skip"), state.context, this.reader.getRef(), e.getClass().getCanonicalName());
                    for (final ListenerImpl listener : state.skipReadListeners) {
                        try {
                            log.debugf(Messages.get("CHAINLINK-014408.chunk.reader.skip.listener"), state.context);
                            listener.onSkipReadItem(state.configuration, state.context, e);
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
                case RETRY:
                case NO_ROLLBACK:
                    ++state.retried;
                    log.debugf(Messages.get("CHAINLINK-014407.chunk.reader.retry"), state.context, this.reader.getRef(), e.getClass().getCanonicalName());
                    for (final ListenerImpl listener : state.retryReadListeners) {
                        try {
                            log.debugf(Messages.get("CHAINLINK-014409.chunk.reader.retry.listener"), state.context);
                            listener.onRetryReadException(state.configuration, state.context, e);
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
                    if (match == Match.NO_ROLLBACK) {
                        log.debugf(Messages.get("CHAINLINK-014411.chunk.reader.no.rollback"), state.context, this.reader.getRef(), e.getClass().getCanonicalName());
                        state.next(READ);
                        return;
                    }
                    _rollbackTransaction(state, e);
                    state.next(BEGIN);
                    return;
                default:
                    throw e;
            }
        }
    }

    private void _processItem(final Object read, final State state) throws Exception {
        Exception exception = null;
        try {
            for (final ListenerImpl listener : state.itemProcessListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014500.chunk.processor.before"), state.context);
                    listener.beforeProcess(state.configuration, state.context, read);
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
            log.debugf(Messages.get("CHAINLINK-014501.chunk.processor.process"), state.context, this.processor.getRef());
            final Object processed = this.processor.processItem(state.configuration, state.context, read);
            if (processed == null) {
                state.stepContext.getMetric(FILTER_COUNT).increment();
                log.debugf(Messages.get("CHAINLINK-014510.chunk.processor.filter"), state.context);
                state.next(state.isReadyToCheckpoint() ? WRITE : READ);
                return;
            }
            for (final ListenerImpl listener : state.itemProcessListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014502.chunk.processor.after"), state.context);
                    listener.afterProcess(state.configuration, state.context, read, processed);
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
            log.warnf(Messages.get("CHAINLINK-014503.chunk.processor.error"), state.context, this.processor.getRef(), e.getClass().getCanonicalName());
            for (final ListenerImpl listener : state.itemProcessListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014508.chunk.processor.error.listener"), state.context);
                    listener.onProcessError(state.configuration, state.context, read, e);
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
            final Match match = _tryMatchException(state, e);
            switch (match) {
                case SKIP:
                    ++state.skipped;
                    state.stepContext.getMetric(PROCESS_SKIP_COUNT).increment();
                    log.debugf(Messages.get("CHAINLINK-014504.chunk.processor.skip"), state.context, this.processor.getRef(), e.getClass().getCanonicalName());
                    for (final ListenerImpl listener : state.skipProcessListeners) {
                        try {
                            log.debugf(Messages.get("CHAINLINK-014506.chunk.processor.skip.listener"), state.context);
                            listener.onSkipProcessItem(state.configuration, state.context, read, e);
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
                case RETRY:
                case NO_ROLLBACK:
                    ++state.retried;
                    log.debugf(Messages.get("CHAINLINK-014505.chunk.processor.retry"), state.context, this.processor.getRef(), e.getClass().getCanonicalName());
                    for (final ListenerImpl listener : state.retryProcessListeners) {
                        try {
                            log.debugf(Messages.get("CHAINLINK-014507.chunk.processor.retry.listener"), state.context);
                            listener.onRetryProcessException(state.configuration, state.context, read, e);
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
                    if (match == Match.NO_ROLLBACK) {
                        log.debugf(Messages.get("CHAINLINK-014509.chunk.processor.no.rollback"), state.context, this.processor.getRef(), e.getClass().getCanonicalName());
                        state.next(PROCESS);
                        return;
                    }
                    _rollbackTransaction(state, e);
                    state.next(BEGIN);
                    return;
                default:
                    throw e;
            }
        }
    }

    private void _writeItem(final State state) throws Exception {
        Exception exception = null;
        try {
            for (final ListenerImpl listener : state.itemWriteListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014602.chunk.writer.before"), state.context, this.writer.getRef());
                    listener.beforeWrite(state.configuration, state.context, state.objects);
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
            log.debugf(Messages.get("CHAINLINK-014603.chunk.writer.write"), state.context, this.writer.getRef());
            this.writer.writeItems(state.configuration, state.context, state.objects);
            state.stepContext.getMetric(WRITE_COUNT).increment(state.objects.size());
            for (final ListenerImpl listener : state.itemWriteListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014604.chunk.writer.after"), state.context);
                    listener.afterWrite(state.configuration, state.context, state.objects);
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
            state.next(AFTER);
        } catch (final Exception e) {
            log.warnf(Messages.get("CHAINLINK-014605.chunk.writer.error"), state.context, this.writer.getRef(), e.getClass().getCanonicalName());
            for (final ListenerImpl listener : state.itemWriteListeners) {
                try {
                    log.debugf(Messages.get("CHAINLINK-014610.chunk.writer.error.listener"), state.context);
                    listener.onWriteError(state.configuration, state.context, state.objects, e);
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
            final Match match = _tryMatchException(state, e);
            switch (match) {
                case SKIP:
                    ++state.skipped;
                    state.stepContext.getMetric(WRITE_SKIP_COUNT).increment();
                    log.debugf(Messages.get("CHAINLINK-014606.chunk.writer.skip"), state.context, this.writer.getRef(), e.getClass().getCanonicalName());
                    for (final ListenerImpl listener : state.skipWriteListeners) {
                        try {
                            log.debugf(Messages.get("CHAINLINK-014608.chunk.writer.skip.listener"), state.context);
                            listener.onSkipWriteItem(state.configuration, state.context, state.objects, e);
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
                    state.next(AFTER);
                    return;
                case RETRY:
                case NO_ROLLBACK:
                    ++state.retried;
                    log.debugf(Messages.get("CHAINLINK-014607.chunk.writer.retry"), state.context, this.writer.getRef(), e.getClass().getCanonicalName());
                    for (final ListenerImpl listener : state.retryWriteListeners) {
                        try {
                            log.debugf(Messages.get("CHAINLINK-014609.chunk.writer.retry.listener"), state.context);
                            listener.onRetryWriteException(state.configuration, state.context, state.objects, e);
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
                    if (match == Match.NO_ROLLBACK) {
                        log.debugf(Messages.get("CHAINLINK-014611.chunk.writer.no.rollback"), state.context, this.writer.getRef(), e.getClass().getCanonicalName());
                        state.next(WRITE);
                        return;
                    }
                    _rollbackTransaction(state, e);
                    state.next(BEGIN);
                    return;
                default:
                    throw e;
            }
        }
    }

    private Match _tryMatchException(final State state, final Exception e) throws Exception {
        if (state.retrying) {
            if (state.isSkipAllowed() && getSkippableExceptionClasses().matches(e)) {
                return Match.SKIP;
            }
            if (state.isRetryAllowed() && getRetryableExceptionClasses().matches(e)) {
                if (getNoRollbackExceptionClasses().matches(e)) {
                    return Match.NO_ROLLBACK;
                }
                return Match.RETRY;
            }
            return Match.NONE;
        } else {
            if (state.isRetryAllowed() && getRetryableExceptionClasses().matches(e)) {
                if (getNoRollbackExceptionClasses().matches(e)) {
                    return Match.NO_ROLLBACK;
                }
                return Match.RETRY;
            }
            if (state.isSkipAllowed() && getSkippableExceptionClasses().matches(e)) {
                return Match.SKIP;
            }
            return Match.NONE;
        }
    }

    private int _readerCheckpoint(final State state) throws Exception {
        try {
            log.debugf(Messages.get("CHAINLINK-014900.chunk.reader.checkpoint"), state.context, this.reader.getRef());
            state.readInfo = this.reader.checkpointInfo(state.configuration, state.context);
        } catch (final Exception e) {
            final Match match = _tryMatchException(state, e);
            switch (match) {
                case SKIP:
                    ++state.skipped;
                    state.stepContext.getMetric(READ_SKIP_COUNT).increment();
                    log.debugf(Messages.get("CHAINLINK-014902.chunk.checkpoint.skip"), state.context, e.getClass().getCanonicalName());
                    break;
                case RETRY:
                case NO_ROLLBACK:
                    ++state.retried;
                    log.debugf(Messages.get("CHAINLINK-014903.chunk.checkpoint.retry"), state.context, e.getClass().getCanonicalName());
                    if (match == Match.NO_ROLLBACK) {
                        log.debugf(Messages.get("CHAINLINK-014904.chunk.checkpoint.no.rollback"), state.context, e.getClass().getCanonicalName());
                        return READER_CHECKPOINT;
                    }
                    _rollbackTransaction(state, e);
                    return BEGIN;
                default:
                    throw e;
            }
        }
        return WRITER_CHECKPOINT;
    }

    private int _writerCheckpoint(final State state) throws Exception {
        try {
            log.debugf(Messages.get("CHAINLINK-014901.chunk.writer.checkpoint"), state.context, this.writer.getRef());
            state.writeInfo = this.writer.checkpointInfo(state.configuration, state.context);
        } catch (final Exception e) {
            final Match match = _tryMatchException(state, e);
            switch (match) {
                case SKIP:
                    ++state.skipped;
                    state.stepContext.getMetric(WRITE_SKIP_COUNT).increment();
                    log.debugf(Messages.get("CHAINLINK-014902.chunk.checkpoint.skip"), state.context, e.getClass().getCanonicalName());
                    break;
                case RETRY:
                case NO_ROLLBACK:
                    ++state.retried;
                    log.debugf(Messages.get("CHAINLINK-014903.chunk.checkpoint.retry"), state.context, e.getClass().getCanonicalName());
                    if (match == Match.NO_ROLLBACK) {
                        log.debugf(Messages.get("CHAINLINK-014904.chunk.checkpoint.no.rollback"), state.context, e.getClass().getCanonicalName());
                        return WRITER_CHECKPOINT;
                    }
                    _rollbackTransaction(state, e);
                    return BEGIN;
                default:
                    throw e;
            }
        }
        return COLLECT;
    }

    private void _commit(final State state) throws Exception {
        log.debugf(Messages.get("CHAINLINK-014102.chunk.transaction.commit"), state.context);
        state.transactionManager.commit();
        state.stepContext.getMetric(COMMIT_COUNT).increment();
        state.endCheckpoint();
        state.commit();
    }

    private void _rollbackTransaction(final State state, final Exception exception) throws Exception {
        state.stepContext.getMetric(ROLLBACK_COUNT).increment();
        try {
            _closeWriter(state);
            _closeReader(state);
        } finally {
            log.debugf(Messages.get("CHAINLINK-014103.chunk.transaction.rollback"), state.context);
            _runErrorListeners(state, exception);
            state.transactionManager.rollback();
            state.endCheckpoint();
        }
        log.debugf(Messages.get("CHAINLINK-014101.chunk.transaction.begin"), state.context);
        final Exception e = state.takeException();
        final Throwable f = state.takeFailure();
        try {
            state.transactionManager.begin();
            _openReader(state);
            _openWriter(state);
            if (!state.isFailed()) {
                log.debugf(Messages.get("CHAINLINK-014102.chunk.transaction.commit"), state.context);
                state.transactionManager.commit();
            } else {
                log.debugf(Messages.get("CHAINLINK-014103.chunk.transaction.rollback"), state.context);
                _runErrorListeners(state, state._exception);
                state.transactionManager.rollback();
            }
        } finally {
            state.give(e, f);
        }
        state.objects.clear();
        state.rollback();
    }

    private void _cleanupTx(final State state) {
        try {
            if (state.isFailed() && state.transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION) {
                log.debugf(Messages.get("CHAINLINK-014103.chunk.transaction.rollback"), state.context);
                _runErrorListeners(state, state._exception);
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
        try {
            if (state.transactionManager.getStatus() == Status.STATUS_ACTIVE) {
                log.debugf(Messages.get("CHAINLINK-014104.chunk.transaction.rollback.only"), state.context);
                state.transactionManager.setRollbackOnly();
            }
        } catch (final SystemException e) {
            exception.addSuppressed(e);
        }
    }

    @Override
    public synchronized void cancel(final Configuration configuration, final ExecutionContext context) {
        if (context != null) {
            final MutableStepContext stepContext = context.getStepContext();
            if (stepContext != null) {
                stepContext.setBatchStatus(BatchStatus.STOPPING);
            }
        }
    }

    private enum Match { NONE, SKIP, RETRY, NO_ROLLBACK }

    private static class State implements CheckpointAlgorithm {
        int next = BEGIN;
        boolean finished = false;
        boolean retrying = false;
        boolean _firstRetry = false;
        boolean _lastRetry = true;
        Object value;
        Serializable readInfo;
        Serializable writeInfo;
        Exception _exception = null;
        Throwable _failure = null;

        final List<Object> objects;

        final long jobExecutionId;
        final long stepExecutionId;
        final Long partitionExecutionId;
        final TransactionManager transactionManager;
        final ExecutionRepository repository;
        final MutableStepContext stepContext;
        final ExecutionContext context;
        final Configuration configuration;
        final Promise<?,Throwable,?> promise;

        final ArrayList<Item> items;

        final int itemCount;
        final long timeLimit;
        final int skipLimit;
        final int retryLimit;

        int skipped = 0;
        int retried = 0;

        long checkpointStartTime;

        final CheckpointAlgorithmImpl _checkpointAlgorithm;
        final CheckpointAlgorithmImpl _retryCheckpointAlgorithm;

        final List<ListenerImpl> chunkListeners;
        final List<ListenerImpl> itemReadListeners;
        final List<ListenerImpl> retryReadListeners;
        final List<ListenerImpl> skipReadListeners;
        final List<ListenerImpl> itemProcessListeners;
        final List<ListenerImpl> retryProcessListeners;
        final List<ListenerImpl> skipProcessListeners;
        final List<ListenerImpl> itemWriteListeners;
        final List<ListenerImpl> retryWriteListeners;
        final List<ListenerImpl> skipWriteListeners;

        private State(final ChunkImpl chunk, final Configuration configuration, final Promise<?,Throwable,?> promise,
                      final ExecutionRepositoryId executionRepositoryId, final ExecutionContext context, final int timeout) throws Exception {
            this.jobExecutionId = context.getJobExecutionId();
            this.stepExecutionId = context.getStepExecutionId();
            this.partitionExecutionId = context.getPartitionExecutionId();
            this.transactionManager = configuration.getTransactionManager();
            this.repository = Repository.getExecutionRepository(configuration, executionRepositoryId);
            this.stepContext = context.getStepContext();
            this.context = context;
            this.configuration = configuration;
            this.promise = promise;
            this.items = new ArrayList<>();

            this.itemCount = Integer.parseInt(chunk.itemCount);
            this.timeLimit = TimeUnit.SECONDS.toMillis(Integer.parseInt(chunk.timeLimit));
            this.skipLimit = chunk.skipLimit == null ? -1 : Integer.parseInt(chunk.skipLimit);
            this.retryLimit = chunk.retryLimit == null ? -1 : Integer.parseInt(chunk.retryLimit);

            if (context.isRestarting()) {
                final BaseExecution execution = this.partitionExecutionId == null
                        ? repository.getPreviousStepExecution(this.jobExecutionId, this.stepExecutionId, this.stepContext.getStepName())
                        : repository.getPartitionExecution(this.partitionExecutionId);
                this.readInfo = execution == null ? null : execution.getReaderCheckpoint();
                this.writeInfo = execution == null ? null : execution.getWriterCheckpoint();
            } else {
                this.readInfo = null;
                this.writeInfo = null;
            }

            this.objects = chunk.checkpointAlgorithm == null
                    ? new LinkedList<>()
                    : new ArrayList<>(this.itemCount);

            if (chunk.reader == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-014002.chunk.reader.null", context));
            }
            if (chunk.writer == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-014003.chunk.writer.null", context));
            }
            if (CheckpointPolicy.ITEM.equalsIgnoreCase(chunk.checkpointPolicy) || chunk.checkpointAlgorithm == null) {
                this._checkpointAlgorithm = new ItemCheckpointAlgorithm(timeout, this.itemCount);
            } else {
                this._checkpointAlgorithm = chunk.checkpointAlgorithm;
            }
            this._retryCheckpointAlgorithm = new ItemCheckpointAlgorithm(timeout, 1);
            this.chunkListeners = chunk.listeners.getListenersImplementing(configuration, context, ChunkListener.class);
            this.itemReadListeners = chunk.listeners.getListenersImplementing(configuration, context, ItemReadListener.class);
            this.retryReadListeners = chunk.listeners.getListenersImplementing(configuration, context, RetryReadListener.class);
            this.skipReadListeners = chunk.listeners.getListenersImplementing(configuration, context, SkipReadListener.class);
            this.itemProcessListeners = chunk.listeners.getListenersImplementing(configuration, context, ItemProcessListener.class);
            this.retryProcessListeners = chunk.listeners.getListenersImplementing(configuration, context, RetryProcessListener.class);
            this.skipProcessListeners = chunk.listeners.getListenersImplementing(configuration, context, SkipProcessListener.class);
            this.itemWriteListeners = chunk.listeners.getListenersImplementing(configuration, context, ItemWriteListener.class);
            this.retryWriteListeners = chunk.listeners.getListenersImplementing(configuration, context, RetryWriteListener.class);
            this.skipWriteListeners = chunk.listeners.getListenersImplementing(configuration, context, SkipWriteListener.class);
        }

        public void rollback() {
            this.retrying = true;
            this._firstRetry = true;
        }

        public void commit() {
            this.retrying = !this._lastRetry;
            this._firstRetry = false;
        }

        @Override
        public int checkpointTimeout() throws Exception {
            if (this.retrying) {
                if (this._firstRetry) {
                    return this._checkpointAlgorithm.checkpointTimeout(this.configuration, this.context);
                }
                return this._retryCheckpointAlgorithm.checkpointTimeout(this.configuration, this.context);
            } else {
                return this._checkpointAlgorithm.checkpointTimeout(this.configuration, this.context);
            }
        }

        @Override
        public void beginCheckpoint() throws Exception {
            if (this.retrying) {
                if (this._firstRetry) {
                    this._checkpointAlgorithm.beginCheckpoint(this.configuration, this.context);
                }
                this._retryCheckpointAlgorithm.beginCheckpoint(this.configuration, this.context);
            } else {
                this._checkpointAlgorithm.beginCheckpoint(this.configuration, this.context);
            }
        }

        @Override
        public boolean isReadyToCheckpoint() throws Exception {
            final boolean ret;
            if (this.retrying) {
                this._lastRetry = this._checkpointAlgorithm.isReadyToCheckpoint(this.configuration, this.context);
                ret = this._retryCheckpointAlgorithm.isReadyToCheckpoint(this.configuration, this.context);
            } else {
                ret = this._checkpointAlgorithm.isReadyToCheckpoint(this.configuration, this.context);
            }
            return ret
                    || (0 != this.timeLimit && this.checkpointStartTime + this.timeLimit < System.currentTimeMillis())
                    || promise.isCancelled();
        }

        @Override
        public void endCheckpoint() throws Exception {
            if (this.retrying) {
                if (this._lastRetry) {
                    this._checkpointAlgorithm.endCheckpoint(this.configuration, this.context);
                }
                this._retryCheckpointAlgorithm.endCheckpoint(this.configuration, this.context);
            } else {
                this._checkpointAlgorithm.endCheckpoint(this.configuration, this.context);
            }
        }

        public void next(final int status) {
            this.next = status;
        }

        public void set(final int status, final Object value) {
            this.next = status;
            this.value = value;
        }

        public void setThrowable(final Throwable e) {
            log.warnf(e, Messages.format("CHAINLINK-014701.chunk.throwable", this.context));
            if (this._failure == null) {
                this._failure = e;
                this.stepContext.setBatchStatus(BatchStatus.FAILED);
            } else {
                this._failure.addSuppressed(e);
            }
        }

        public void setException(final Exception e) {
            log.warnf(e, Messages.format("CHAINLINK-014700.chunk.exception", this.context));
            if (this._exception == null) {
                this._exception = e;
                this.stepContext.setException(e);
                this.stepContext.setBatchStatus(BatchStatus.FAILED);
            } else {
                this._exception.addSuppressed(e);
            }
        }

        Exception takeException() {
            final Exception that = this._exception;
            this._exception = null;
            return that;
        }

        Throwable takeFailure() {
            final Throwable that = this._failure;
            this._failure = null;
            return that;
        }

        void give(final Exception e, final Throwable f) {
            if (e != null) {
                final Exception oldE = this._exception;
                this._exception = e;
                if (oldE != null) {
                    e.addSuppressed(oldE);
                }
            }
            if (f != null) {
                final Throwable oldF = this._failure;
                this._failure = f;
                if (oldF != null) {
                    f.addSuppressed(oldF);
                }
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
                return new BatchRuntimeException(Messages.format("CHAINLINK-014001.chunk.failed", this.context), this._failure);
            }
            return null;
        }
    }
}
