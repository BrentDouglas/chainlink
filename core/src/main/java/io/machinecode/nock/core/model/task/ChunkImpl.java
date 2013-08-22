package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.task.Chunk;

import javax.batch.api.chunk.listener.ChunkListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkImpl implements Chunk {

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
    private final ResolvableService<ChunkListener> listeners;

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
        final ExceptionClassFilterImpl noRollbackExceptionClasses
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
        this.listeners = new ResolvableService<ChunkListener>(ChunkListener.class);
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
}
