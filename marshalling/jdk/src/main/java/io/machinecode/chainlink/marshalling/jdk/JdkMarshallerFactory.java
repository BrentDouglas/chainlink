package io.machinecode.chainlink.marshalling.jdk;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.MarshallerFactory;
import io.machinecode.chainlink.spi.marshalling.Marshaller;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdkMarshallerFactory implements MarshallerFactory {
    @Override
    public Marshaller produce(final BaseConfiguration configuration) throws Exception {
        return new JdkMarshaller();
    }
}
