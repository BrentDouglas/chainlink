package io.machinecode.chainlink.tck.core.transport;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.transport.coherence.CoherenceRegistry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceRegistryFactory implements RegistryFactory {

    @Override
    public CoherenceRegistry produce(final RegistryConfiguration configuration) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                CacheFactory.shutdown();
            }
        });
        return new CoherenceRegistry(
                configuration,
                "InvocationService"
        );
    }
}
