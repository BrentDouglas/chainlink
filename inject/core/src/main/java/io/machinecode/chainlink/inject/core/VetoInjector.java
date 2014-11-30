package io.machinecode.chainlink.inject.core;

import io.machinecode.chainlink.spi.inject.Injector;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class VetoInjector implements Injector {

    @Override
    public boolean inject(final Object bean) throws Exception {
        return true;
    }
}
