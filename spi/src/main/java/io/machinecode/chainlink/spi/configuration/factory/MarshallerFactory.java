package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.marshalling.Marshaller;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MarshallerFactory extends Factory<Marshaller, BaseConfiguration> {
}
