package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.task.ItemReader;

import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.SkipReadListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemReader> implements ItemReader {

    private final ResolvableService<ItemReadListener> listeners;
    private final ResolvableService<RetryReadListener> retryListeners;
    private final ResolvableService<SkipReadListener> skipListeners;

    public ItemReaderImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<javax.batch.api.chunk.ItemReader>(ref, javax.batch.api.chunk.ItemReader.class), properties);
        this.listeners = new ResolvableService<ItemReadListener>(ItemReadListener.class);
        this.retryListeners = new ResolvableService<RetryReadListener>(RetryReadListener.class);
        this.skipListeners = new ResolvableService<SkipReadListener>(SkipReadListener.class);
    }
}
