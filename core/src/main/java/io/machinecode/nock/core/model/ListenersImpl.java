package io.machinecode.nock.core.model;


import io.machinecode.nock.spi.element.Listeners;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersImpl<T> implements Listeners {

    private final List<ListenerImpl<T>> listeners;

    public ListenersImpl(final List<ListenerImpl<T>> listeners) {
        if (listeners == null) {
            this.listeners = Collections.emptyList();
        } else {
            this.listeners = listeners;
        }
    }

    @Override
    public List<ListenerImpl<T>> getListeners() {
        return this.listeners;
    }
}
