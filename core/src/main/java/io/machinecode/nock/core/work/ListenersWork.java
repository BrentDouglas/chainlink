package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.element.Listeners;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersWork<T> implements Work, Listeners {

    private final List<ListenerWork<T>> listeners;

    public ListenersWork(final List<ListenerWork<T>> listeners) {
        this.listeners = listeners;
    }

    @Override
    public List<ListenerWork<T>> getListeners() {
        return this.listeners;
    }
}
