package io.machinecode.nock.jsl.impl;


import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersImpl implements Listeners {

    private final List<Listener> listeners;

    public ListenersImpl(final Listeners that) {
        this.listeners = new ArrayList<Listener>(that.getListeners().size());
        for (final Listener listener : that.getListeners()) {
            this.listeners.add(new ListenerImpl(listener));
        }
    }

    @Override
    public List<Listener> getListeners() {
        return this.listeners;
    }
}
