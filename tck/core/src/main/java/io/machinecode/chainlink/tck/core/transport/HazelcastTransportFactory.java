package io.machinecode.chainlink.tck.core.transport;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.hazelcast.HazelcastTransport;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastTransportFactory implements TransportFactory {

    @Override
    public HazelcastTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        final IExecutorService executor = hazelcast.getExecutorService("chainlink-tck-hazelcast-executor");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hazelcast.shutdown();
            }
        });
        return new HazelcastTransport(
                dependencies,
                properties,
                hazelcast,
                executor
        );
    }
}
