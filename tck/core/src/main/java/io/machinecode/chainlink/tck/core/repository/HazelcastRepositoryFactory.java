package io.machinecode.chainlink.tck.core.repository;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import io.machinecode.chainlink.repository.hazelcast.HazelcastRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastRepositoryFactory implements RepositoryFactory {
    @Override
    public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hazelcast.shutdown();
            }
        });
        return new HazelcastRepository(
                dependencies.getMarshalling(),
                hazelcast
        );
    }
}
