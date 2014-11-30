package io.machinecode.chainlink.marshalling.jdk;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.marshalling.MarshallingProvider;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JdkMarshallingProviderFactory implements MarshallingProviderFactory {
    @Override
    public MarshallingProvider produce(final Configuration configuration) {
        return new JdkMarshallingProvider();
    }
}
