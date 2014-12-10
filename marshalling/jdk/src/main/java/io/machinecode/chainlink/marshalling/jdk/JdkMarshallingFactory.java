package io.machinecode.chainlink.marshalling.jdk;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.marshalling.Marshalling;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JdkMarshallingFactory implements MarshallingFactory {
    @Override
    public Marshalling produce(final Dependencies dependencies, final Properties properties) {
        return new JdkMarshalling();
    }
}
