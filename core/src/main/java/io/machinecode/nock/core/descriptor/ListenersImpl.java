package io.machinecode.nock.core.descriptor;


import io.machinecode.nock.spi.element.Listeners;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersImpl implements Listeners {

    private final List<ListenerImpl> listeners;

    public ListenersImpl(final List<ListenerImpl> listeners) {
        if (listeners == null) {
            this.listeners = Collections.emptyList();
        } else {
            this.listeners = listeners;
        }
    }

    @Override
    public List<ListenerImpl> getListeners() {
        return this.listeners;
    }
}
