package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LocalTransportFactory implements TransportFactory {
    @Override
    public Transport<?> produce(final TransportConfiguration configuration) throws Exception {
        return new LocalTransport(configuration);
    }
}
