package io.machinecode.chainlink.tck.batch;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.repository.infinispan.InfinispanExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
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
public class InfinispanBatchConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() {
        final LocalTransactionManager transactionManager = new LocalTransactionManager(180, TimeUnit.SECONDS);
        return new Builder()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setExecutionRepository(new InfinispanExecutionRepository(
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
                                                return transactionManager;
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
                        transactionManager
                ))
                .setTransactionManager(transactionManager)
                .setExecutorFactoryClass(EventedExecutorFactory.class)
                .setProperty(Constants.EXECUTOR_THREAD_POOL_SIZE, "8")
                .build();
    }
}
