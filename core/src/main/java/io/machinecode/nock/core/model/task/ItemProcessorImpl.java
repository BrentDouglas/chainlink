package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.task.ItemProcessor;

import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.SkipProcessListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemProcessor> implements ItemProcessor {

    private final ResolvableService<ItemProcessListener> listeners;
    private final ResolvableService<RetryProcessListener> retryListeners;
    private final ResolvableService<SkipProcessListener> skipListeners;

    public ItemProcessorImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<javax.batch.api.chunk.ItemProcessor>(ref, javax.batch.api.chunk.ItemProcessor.class), properties);
        this.listeners = new ResolvableService<ItemProcessListener>(ItemProcessListener.class);
        this.retryListeners = new ResolvableService<RetryProcessListener>(RetryProcessListener.class);
        this.skipListeners = new ResolvableService<SkipProcessListener>(SkipProcessListener.class);
    }
}
