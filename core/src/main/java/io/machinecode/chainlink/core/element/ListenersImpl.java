package io.machinecode.chainlink.core.element;


import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.Listeners;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
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