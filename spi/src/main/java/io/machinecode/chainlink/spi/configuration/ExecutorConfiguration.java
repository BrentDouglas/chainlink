package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.registry.Registry;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExecutorConfiguration extends RegistryConfiguration {

    Registry getRegistry();
}
