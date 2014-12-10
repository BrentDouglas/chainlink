package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;

import javax.transaction.TransactionManager;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanTransportFactory implements TransportFactory {

    @Override
    public InfinispanTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
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
                                return dependencies.getTransactionManager();
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
        return new InfinispanTransport(
                dependencies,
                properties,
                manager,
                30,
                TimeUnit.SECONDS
        );
    }
}
