package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Factory<T> {

    T produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception;
}
