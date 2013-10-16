package io.machinecode.nock.core.local;

import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.transport.TransportFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransportFactory implements TransportFactory {

    @Override
    public Transport produce(final RuntimeConfiguration configuration, final int threads) {
        return new LocalTransport(configuration, threads);
    }
}
