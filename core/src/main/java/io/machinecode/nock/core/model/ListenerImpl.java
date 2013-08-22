package io.machinecode.nock.core.model;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.Listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl<T> extends PropertyReferenceImpl<T> implements Listener {

    public ListenerImpl(final ResolvableReference<T> ref, final PropertiesImpl properties) {
        super(ref, properties);
    }
}
