package io.machinecode.chainlink.inject.seam;

import io.machinecode.chainlink.spi.inject.Injector;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SeamInjector implements Injector {

    // Stop the DefaultInjector from injecting the proxy as SeamInjectorInterceptor
    // will inject the actual bean at call time.
    @Override
    public boolean inject(final Object bean) throws Exception {
        return true;
    }
}
