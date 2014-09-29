package io.machinecode.chainlink.tck.core.transport;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.transport.hazelcast.HazelcastRegistry;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastRegistryFactory implements RegistryFactory {

    @Override
    public HazelcastRegistry produce(final RegistryConfiguration configuration) throws Exception {
        final HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance();
        final IExecutorService executor = hazelcast.getExecutorService("chainlink-tck-hazelcast-executor");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                hazelcast.shutdown();
            }
        });
        return new HazelcastRegistry(
                configuration,
                hazelcast,
                executor
        );
    }
}
