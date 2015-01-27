package io.machinecode.chainlink.transport.coherence;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.transport.Transport;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceTransportFactory implements TransportFactory {
    @Override
    public Transport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new CoherenceTransport(dependencies, properties);
    }
}
