package io.machinecode.nock.core.model;


import io.machinecode.nock.spi.element.Listeners;
import io.machinecode.nock.spi.inject.InjectionContext;

import java.util.ArrayList;
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

    public <X> List<X> getListenersImplementing(final InjectionContext context, final Class<X> clazz) {
        final List<X> ret = new ArrayList<X>(this.listeners.size());
        for (final ListenerImpl listener : this.listeners) {
            final X that = listener.load(clazz, context);
            if (that == null) {
                continue;
            }
            if (clazz.isAssignableFrom(that.getClass())) {
                ret.add(that);
            }
        }
        return ret;
    }
}
