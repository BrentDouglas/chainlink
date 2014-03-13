package io.machinecode.chainlink.inject.core;

import io.machinecode.chainlink.spi.inject.Injector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class VetoInjector implements Injector {

    @Override
    public boolean inject(final Object bean) throws Exception {
        return true;
    }
}
