package io.machinecode.chainlink.transport.hazelcast.test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.transport.hazelcast.HazelcastTransport;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestHazelcastTransportFactory implements TestTransportFactory {

    final HazelcastInstance hazelcast;
    final IExecutorService executor;

    public TestHazelcastTransportFactory() {
        this.hazelcast = Hazelcast.newHazelcastInstance();
        this.executor = hazelcast.getExecutorService("chainlink-test-hazelcast-executor");
    }

    @Override
    public HazelcastTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new HazelcastTransport(
                dependencies,
                properties,
                this.hazelcast,
                this.executor
        );
    }

    @Override
    public void close() {
        this.executor.shutdown();
        this.hazelcast.shutdown();
    }
}
