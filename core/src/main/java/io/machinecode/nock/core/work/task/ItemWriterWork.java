package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.Properties;

import javax.batch.api.chunk.ItemWriter;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipWriteListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterWork implements io.machinecode.nock.spi.element.task.ItemWriter {

    private final ResolvableReference<ItemWriter> itemWriter;
    private final ResolvableService<ItemWriteListener> listeners;
    private final ResolvableService<RetryWriteListener> retryListeners;
    private final ResolvableService<SkipWriteListener> skipListeners;

    public ItemWriterWork(final ResolvableReference<ItemWriter> itemWriter) {
        this.itemWriter = itemWriter;
        this.listeners = new ResolvableService<ItemWriteListener>(ItemWriteListener.class);
        this.retryListeners = new ResolvableService<RetryWriteListener>(RetryWriteListener.class);
        this.skipListeners = new ResolvableService<SkipWriteListener>(SkipWriteListener.class);
    }

    @Override
    public String getRef() {
        return this.itemWriter.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
