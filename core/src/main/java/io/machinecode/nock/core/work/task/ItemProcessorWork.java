package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.Properties;

import javax.batch.api.chunk.ItemProcessor;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.SkipProcessListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorWork implements io.machinecode.nock.spi.element.task.ItemProcessor {

    private final ResolvableReference<ItemProcessor> itemProcessor;
    private final ResolvableService<ItemProcessListener> listeners;
    private final ResolvableService<RetryProcessListener> retryListeners;
    private final ResolvableService<SkipProcessListener> skipListeners;

    public ItemProcessorWork(final ResolvableReference<ItemProcessor> itemProcessor) {
        this.itemProcessor = itemProcessor;
        this.listeners = new ResolvableService<ItemProcessListener>(ItemProcessListener.class);
        this.retryListeners = new ResolvableService<RetryProcessListener>(RetryProcessListener.class);
        this.skipListeners = new ResolvableService<SkipProcessListener>(SkipProcessListener.class);
    }

    @Override
    public String getRef() {
        return this.itemProcessor.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
