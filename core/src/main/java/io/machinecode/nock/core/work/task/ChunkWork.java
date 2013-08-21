package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.core.work.partition.CheckpointAlgorithmWork;
import io.machinecode.nock.spi.element.task.Chunk;

import javax.batch.api.chunk.listener.ChunkListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkWork implements Work, Chunk {

    private final String checkpointPolicy;
    private final String itemCount;
    private final String timeLimit;
    private final String skipLimit;
    private final String retryLimit;
    private final ItemReaderWork itemReader;
    private final ItemProcessorWork itemProcessor;
    private final ItemWriterWork itemWriter;
    private final CheckpointAlgorithmWork checkpointAlgorithm;
    private final ExceptionClassFilterWork skippableExceptionClasses;
    private final ExceptionClassFilterWork retryableExceptionClasses;
    private final ExceptionClassFilterWork noRollbackExceptionClasses;
    private final ResolvableService<ChunkListener> listeners;

    public ChunkWork(final String checkpointPolicy, final String itemCount, final String timeLimit, final String skipLimit,
                     final String retryLimit, final ItemReaderWork itemReader, final ItemProcessorWork itemProcessor,
                     final ItemWriterWork itemWriter, final CheckpointAlgorithmWork checkpointAlgorithm,
                     final ExceptionClassFilterWork skippableExceptionClasses, final ExceptionClassFilterWork retryableExceptionClasses,
                     final ExceptionClassFilterWork noRollbackExceptionClasses) {
        this.checkpointPolicy = checkpointPolicy;
        this.itemCount = itemCount;
        this.timeLimit = timeLimit;
        this.skipLimit = skipLimit;
        this.retryLimit = retryLimit;
        this.itemReader = itemReader;
        this.itemProcessor = itemProcessor;
        this.itemWriter = itemWriter;
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
    public ItemReaderWork getReader() {
        return this.itemReader;
    }

    @Override
    public ItemProcessorWork getProcessor() {
        return this.itemProcessor;
    }

    @Override
    public ItemWriterWork getWriter() {
        return this.itemWriter;
    }

    @Override
    public CheckpointAlgorithmWork getCheckpointAlgorithm() {
        return this.checkpointAlgorithm;
    }

    @Override
    public ExceptionClassFilterWork getSkippableExceptionClasses() {
        return this.skippableExceptionClasses;
    }

    @Override
    public ExceptionClassFilterWork getRetryableExceptionClasses() {
        return this.retryableExceptionClasses;
    }

    @Override
    public ExceptionClassFilterWork getNoRollbackExceptionClasses() {
        return this.noRollbackExceptionClasses;
    }
}
