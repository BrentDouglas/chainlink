package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.registry.Registry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutorConfiguration extends RegistryConfiguration {

    Registry getRegistry();
}
