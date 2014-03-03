package io.machinecode.nock.core.model;


import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.Listeners;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.loader.ArtifactOfWrongTypeException;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersImpl implements Listeners {

    private static final Logger log = Logger.getLogger(ListenersImpl.class);

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

    public List<ListenerImpl> getListenersImplementing(final Executor executor, final ExecutionContext context, final Class<?> clazz) throws Exception {
        final List<ListenerImpl> ret = new ArrayList<ListenerImpl>(this.listeners.size());
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            for (final ListenerImpl listener : this.listeners) {
                provider.setInjectables(listener._injectables(context));

                final Object that;
                try {
                    that = listener.load(clazz, injectionContext, context);
                } catch (final ArtifactOfWrongTypeException e) {
                    continue;
                }
                if (that == null) {
                    continue;
                }
                if (clazz.isAssignableFrom(that.getClass())) {
                    ret.add(listener);
                }
            }
        } finally {
            provider.setInjectables(null);
        }
        return ret;
    }
}
