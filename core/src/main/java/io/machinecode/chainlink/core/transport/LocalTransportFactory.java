package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.transport.Transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransportFactory implements TransportFactory {
    @Override
    public Transport produce(final TransportConfiguration configuration) throws Exception {
        return new LocalTransport();
    }
}
