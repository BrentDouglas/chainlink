package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.inject.ResolvableService;
import io.machinecode.nock.spi.element.task.ItemWriter;

import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipWriteListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemWriter> implements ItemWriter {

    private final ResolvableService<ItemWriteListener> listeners;
    private final ResolvableService<RetryWriteListener> retryListeners;
    private final ResolvableService<SkipWriteListener> skipListeners;

    public ItemWriterImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<javax.batch.api.chunk.ItemWriter>(ref, javax.batch.api.chunk.ItemWriter.class), properties);
        this.listeners = new ResolvableService<ItemWriteListener>(ItemWriteListener.class);
        this.retryListeners = new ResolvableService<RetryWriteListener>(RetryWriteListener.class);
        this.skipListeners = new ResolvableService<SkipWriteListener>(SkipWriteListener.class);
    }
}
