package io.machinecode.chainlink.tck.core.transport;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.hazelcast.HazelcastTransport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastTransportFactory implements TransportFactory {

    @Override
    public HazelcastTransport produce(final TransportConfiguration configuration) throws Exception {
        final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        final IExecutorService executor = hazelcast.getExecutorService("chainlink-tck-hazelcast-executor");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hazelcast.shutdown();
            }
        });
        return new HazelcastTransport(
                configuration,
                hazelcast,
                executor
        );
    }
}
