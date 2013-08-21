package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.Properties;

import javax.batch.api.chunk.ItemReader;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.SkipReadListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderWork implements io.machinecode.nock.spi.element.task.ItemReader {

    private final ResolvableReference<ItemReader> itemReader;
    private final ResolvableService<ItemReadListener> listeners;
    private final ResolvableService<RetryReadListener> retryListeners;
    private final ResolvableService<SkipReadListener> skipListeners;

    public ItemReaderWork(final ResolvableReference<ItemReader> itemReader) {
        this.itemReader = itemReader;
        this.listeners = new ResolvableService<ItemReadListener>(ItemReadListener.class);
        this.retryListeners = new ResolvableService<RetryReadListener>(RetryReadListener.class);
        this.skipListeners = new ResolvableService<SkipReadListener>(SkipReadListener.class);
    }

    @Override
    public String getRef() {
        return this.itemReader.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
