package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.serialization.Serializer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface SerializerFactory extends Factory<Serializer, BaseConfiguration> {
}
