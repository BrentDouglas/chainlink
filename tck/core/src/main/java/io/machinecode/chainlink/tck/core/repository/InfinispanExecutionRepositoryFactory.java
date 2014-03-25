package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.infinispan.InfinispanExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
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
public class InfinispanExecutionRepositoryFactory implements ExecutionRepositoryFactory {
    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) {
        return new InfinispanExecutionRepository(
                configuration.getClassLoader(),
                new DefaultCacheManager(
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
                                .lockAcquisitionTimeout(TimeUnit.SECONDS.toMillis(30))
                                .clustering()
                                .cacheMode(CacheMode.LOCAL)
                                .sync()
                                .build()
                ),
                configuration.getTransactionManager()
        );
    }
}
