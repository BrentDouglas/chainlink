package io.machinecode.chainlink.tck.coherence.transport;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.coherence.CoherenceTransport;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceTransportFactory implements TransportFactory {

    @Override
    public CoherenceTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                CacheFactory.shutdown();
            }
        });
        return new CoherenceTransport(
                dependencies,
                properties,
                "InvocationService"
        );
    }
}
