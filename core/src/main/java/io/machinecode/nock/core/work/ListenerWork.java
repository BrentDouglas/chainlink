package io.machinecode.nock.core.work;

import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerWork<T> implements Work, Listener {

    private final ResolvableReference<T> listener;

    public ListenerWork(final ResolvableReference<T> listener) {
        this.listener = listener;
    }

    @Override
    public String getRef() {
        return this.listener.ref();
    }

    @Override
    public Properties getProperties() {
        return null;
    }
}
