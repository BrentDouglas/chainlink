package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
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
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class InfinispanRegistryFactory implements RegistryFactory {

    @Override
    public InfinispanRegistry produce(final RegistryConfiguration configuration) throws Exception {
        final DefaultCacheManager manager = new DefaultCacheManager(
                new GlobalConfigurationBuilder()
                        .clusteredDefault()
                        .globalJmxStatistics()
                        .jmxDomain("io.machinecode.chainlink.test")
                        .allowDuplicateDomains(true)
                        .asyncListenerExecutor()
                        .addProperty("maxThreads", "1")
                        .build(),
                new ConfigurationBuilder()
                        .deadlockDetection()
                        .enable()
                        .invocationBatching()
                        .disable()
                        .transaction()
                        .lockingMode(LockingMode.PESSIMISTIC)
                        .transactionMode(TransactionMode.TRANSACTIONAL)
                        .transactionManagerLookup(new TransactionManagerLookup() {
                            @Override
                            public TransactionManager getTransactionManager() throws Exception {
                                return configuration.getTransactionManager();
                            }
                        })
                        .locking()
                        .isolationLevel(IsolationLevel.READ_COMMITTED)
                        .lockAcquisitionTimeout(30, TimeUnit.SECONDS)
                        .clustering()
                        .cacheMode(CacheMode.DIST_SYNC)
                        .sync()
                        .build()
        );
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                manager.stop();
            }
        });
        return new InfinispanRegistry(
                configuration,
                manager,
                30,
                TimeUnit.SECONDS
        );
    }
}
