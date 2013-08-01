package io.machinecode.nock.jsl.impl;


import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;
import io.machinecode.nock.jsl.impl.Util.Transformer;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersImpl implements Listeners {

    private static final Transformer<Listener> LISTENER_TRANSFORMER = new Transformer<Listener>() {
        @Override
        public Listener transform(final Listener that) {
            return new ListenerImpl(that);
        }
    };

    private final List<Listener> listeners;

    public ListenersImpl(final Listeners that) {
        if (that == null) {
            this.listeners = Collections.emptyList();
        } else {
            this.listeners = Util.immutableCopy(that.getListeners(), LISTENER_TRANSFORMER);
        }
    }

    @Override
    public List<Listener> getListeners() {
        return this.listeners;
    }
}
