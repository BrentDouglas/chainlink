package io.machinecode.nock.jsl.fluent;


import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentListeners implements Listeners {

    private final List<Listener> listeners = new ArrayList<Listener>(0);

    @Override
    public List<Listener> getListeners() {
        return this.listeners;
    }

    public FluentListeners addListener(final Listener listener) {
        this.listeners.add(listener);
        return this;
    }
}
