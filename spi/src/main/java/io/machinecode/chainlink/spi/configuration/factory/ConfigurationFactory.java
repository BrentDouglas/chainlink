package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ConfigurationFactory {

    String getId();

    ConfigurationBuilder<?> produce(final ClassLoader loader) throws Exception;
}
