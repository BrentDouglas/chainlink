package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InjectablesProviderImpl implements InjectablesProvider {

    private static final Logger log = Logger.getLogger(InjectablesProviderImpl.class);

    private static final ThreadLocal<Injectables> injectables = new ThreadLocal<>();

    @Override
    public void setInjectables(final Injectables injectables) {
        InjectablesProviderImpl.injectables.set(injectables);
    }

    @Override
    public Injectables getInjectables() {
        return injectables.get();
    }
}
