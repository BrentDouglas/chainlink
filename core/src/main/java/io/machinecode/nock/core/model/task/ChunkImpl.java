package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.task.CheckpointAlgorithm;
import io.machinecode.nock.jsl.api.task.Chunk;
import io.machinecode.nock.jsl.api.task.ExceptionClassFilter;
import io.machinecode.nock.jsl.api.task.ItemProcessor;
import io.machinecode.nock.jsl.api.task.ItemReader;
import io.machinecode.nock.jsl.api.task.ItemWriter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkImpl implements Chunk {

    private final String checkpointPolicy;
    private final String itemCount;
    private final String timeLimit;
    private final String skipLimit;
    private final String retryLimit;
    private final ItemReader reader;
    private final ItemProcessor processor;
    private final ItemWriter writer;
    private final CheckpointAlgorithm checkpointAlgorithm;
    private final ExceptionClassFilter skippableExceptionClasses;
    private final ExceptionClassFilter retryableExceptionClasses;
    private final ExceptionClassFilter noRollbackExceptionClasses;

    public ChunkImpl(
        final String checkpointPolicy,
        final String itemCount,
        final String timeLimit,
        final String skipLimit,
        final String retryLimit,
        final ItemReader reader,
        final ItemProcessor processor,
        final ItemWriter writer,
        final CheckpointAlgorithm checkpointAlgorithm,
        final ExceptionClassFilter skippableExceptionClasses,
        final ExceptionClassFilter retryableExceptionClasses,
        final ExceptionClassFilter noRollbackExceptionClasses
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
    public ItemReader getReader() {
        return this.reader;
    }

    @Override
    public ItemProcessor getProcessor() {
        return this.processor;
    }

    @Override
    public ItemWriter getWriter() {
        return this.writer;
    }

    @Override
    public CheckpointAlgorithm getCheckpointAlgorithm() {
        return this.checkpointAlgorithm;
    }

    @Override
    public ExceptionClassFilter getSkippableExceptionClasses() {
        return this.skippableExceptionClasses;
    }

    @Override
    public ExceptionClassFilter getRetryableExceptionClasses() {
        return this.retryableExceptionClasses;
    }

    @Override
    public ExceptionClassFilter getNoRollbackExceptionClasses() {
        return this.noRollbackExceptionClasses;
    }
}
