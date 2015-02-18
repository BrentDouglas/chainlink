package io.machinecode.chainlink.transport.infinispan.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.jgroups.JChannel;
import org.jgroups.util.Util;

import javax.transaction.TransactionManager;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestInfinispanTransportFactory implements TestTransportFactory {

    DefaultCacheManager manager;
    JChannel channel;

    @Override
    public InfinispanTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        manager = new DefaultCacheManager(
                new GlobalConfigurationBuilder()
                        .clusteredDefault()
                        .transport()
                        .clusterName("chainlink-test-cluster")
                        .transport(new JGroupsTransport(channel = new JChannel(Util.getTestStack())))
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
        return new InfinispanTransport(
                dependencies,
                properties,
                manager,
                30,
                TimeUnit.SECONDS
        ) {
            @Override
            public void open(final Configuration configuration) throws Exception {
                this.configuration = configuration;
                manager.getGlobalComponentRegistry().registerComponent(configuration, Configuration.class);
                doOpen();
            }
        };
    }

    @Override
    public void close() {
        manager.stop();
    }
}
