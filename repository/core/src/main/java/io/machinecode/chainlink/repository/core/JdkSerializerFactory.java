package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.SerializerFactory;
import io.machinecode.chainlink.spi.serialization.Serializer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdkSerializerFactory implements SerializerFactory {
    @Override
    public Serializer produce(final BaseConfiguration configuration) throws Exception {
        return new JdkSerializer();
    }
}
