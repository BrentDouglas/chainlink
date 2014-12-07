package io.machinecode.chainlink.tck.core.repository;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastExecutonRepository;
import io.machinecode.chainlink.spi.configuration.ExecutionRepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final ExecutionRepositoryConfiguration configuration) throws Exception {
        final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hazelcast.shutdown();
            }
        });
        return new HazelcastExecutonRepository(
                configuration.getMarshallingProviderFactory().produce(configuration),
                hazelcast
        );
    }
}
