package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.commons.util.FileLookupFactory;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.remoting.rpc.ResponseMode;
import org.infinispan.remoting.rpc.RpcOptions;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.jgroups.JChannel;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanTransportFactory implements TransportFactory {
    @Override
    public Transport produce(final TransportConfiguration configuration) throws Exception {
        return new InfinispanTransport(
                configuration,
                new DefaultCacheManager(
                        new GlobalConfigurationBuilder()
                                .clusteredDefault()
                                .globalJmxStatistics()
                                    .jmxDomain("io.machinecode.chainlink.test")
                                    .allowDuplicateDomains(true)
                                .asyncListenerExecutor()
                                    .addProperty("maxThreads", "1")
                                //.transport()
                                    //.addProperty(JGroupsTransport.CONFIGURATION_FILE, "jgroups.xml")
                                    //.transport(new JGroupsTransport(new JChannel(FileLookupFactory.newInstance().lookupFileLocation("jgroups-tcp.xml", DefaultCacheManager.class.getClassLoader()))))
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
                                    .lockAcquisitionTimeout(TimeUnit.SECONDS.toMillis(30))
                                .clustering()
                                    .cacheMode(CacheMode.DIST_SYNC)
                                .sync()
                                .build()
                ),
                new RpcOptions(
                        10,
                        TimeUnit.SECONDS,
                        null,
                        ResponseMode.SYNCHRONOUS,
                        false,
                        true,
                        true
                )
        );
    }
}
