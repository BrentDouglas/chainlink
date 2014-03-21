package io.machinecode.chainlink.tck.spring;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.repository.infinispan.InfinispanExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.eviction.EvictionStrategy;
import org.infinispan.eviction.EvictionThreadPolicy;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanSpringConfigurationFactory implements ConfigurationFactory {

    private static AbstractApplicationContext context;

    static {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }

    @Override
    public Configuration produce() {
        final LocalTransactionManager transactionManager = new LocalTransactionManager(180, TimeUnit.SECONDS);
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new InfinispanExecutionRepository(
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
                .setArtifactLoaders(context.getBean(SpringArtifactLoader.class))
                .build();
    }
}
