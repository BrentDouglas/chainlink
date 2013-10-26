package io.machinecode.nock.core.inject;

import io.machinecode.nock.spi.inject.Injectables;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InjectablesProviderImpl implements InjectablesProvider {

    private static final Logger log = Logger.getLogger(InjectablesProviderImpl.class);

    private static final ThreadLocal<Injectables> injectables = new ThreadLocal<Injectables>();

    @Override
    public void setInjectables(final Injectables injectables) {
        InjectablesProviderImpl.injectables.set(injectables);
    }

    @Override
    public Injectables getInjectables() {
        return injectables.get();
    }
}
