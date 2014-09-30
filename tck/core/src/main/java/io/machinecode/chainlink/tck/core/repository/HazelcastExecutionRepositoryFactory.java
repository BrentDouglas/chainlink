package io.machinecode.chainlink.tck.core.repository;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastExecutonRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) throws Exception {
        final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hazelcast.shutdown();
            }
        });
        return new HazelcastExecutonRepository(
                configuration.getMarshallerFactory().produce(configuration),
                hazelcast
        );
    }
}
