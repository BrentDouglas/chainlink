package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.spi.inject.InjectablesProvider;

import java.security.PrivilegedAction;
import java.util.ServiceLoader;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class LoadProviders implements PrivilegedAction<ServiceLoader<InjectablesProvider>> {
    @Override
    public ServiceLoader<InjectablesProvider> run() {
        return ServiceLoader.load(InjectablesProvider.class);
    }
}
