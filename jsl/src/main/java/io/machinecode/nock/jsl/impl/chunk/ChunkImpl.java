package io.machinecode.nock.jsl.impl.chunk;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.chunk.CheckpointAlgorithm;
import io.machinecode.nock.jsl.api.chunk.Chunk;
import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;
import io.machinecode.nock.jsl.api.chunk.ItemProcessor;
import io.machinecode.nock.jsl.api.chunk.ItemReader;
import io.machinecode.nock.jsl.api.chunk.ItemWriter;
import io.machinecode.nock.jsl.impl.PropertiesImpl;

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
    private final Properties properties;

    public ChunkImpl(final Chunk that) {
        this.checkpointPolicy = that.getCheckpointPolicy();
        this.itemCount = that.getItemCount();
        this.timeLimit = that.getTimeLimit();
        this.skipLimit = that.getSkipLimit();
        this.retryLimit = that.getRetryLimit();
        this.reader = new ItemReaderImpl(that.getReader());
        this.processor = new ItemProcessorImpl(that.getProcessor());
        this.writer = new ItemWriterImpl(that.getWriter());
        this.checkpointAlgorithm = new CheckpointAlgorithmImpl(that.getCheckpointAlgorithm());
        this.skippableExceptionClasses = new ExceptionClassFilterImpl(that.getSkippableExceptionClasses());
        this.retryableExceptionClasses = new ExceptionClassFilterImpl(that.getRetryableExceptionClasses());
        this.noRollbackExceptionClasses = new ExceptionClassFilterImpl(that.getNoRollbackExceptionClasses());
        this.properties = new PropertiesImpl(that.getProperties());
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

    @Override
    public Properties getProperties() {
        return this.properties;
    }
}
