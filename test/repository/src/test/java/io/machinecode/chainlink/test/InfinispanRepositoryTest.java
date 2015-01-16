package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.infinispan.InfinispanExecutionRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.junit.After;

import javax.transaction.TransactionManager;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanRepositoryTest extends RepositoryTest {

    private EmbeddedCacheManager cacheManager;
    
    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getExecutionRepository().setFactory(new ExecutionRepositoryFactory() {
            @Override
            public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new InfinispanExecutionRepository(
                        dependencies.getMarshalling(),
                        cacheManager = new DefaultCacheManager(
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
                                        .lockAcquisitionTimeout(TimeUnit.SECONDS.toMillis(30))
                                        .clustering()
                                        .cacheMode(CacheMode.LOCAL)
                                        .sync()
                                        .build()
                        )
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".ids");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".jobInstances");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutions");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".stepExecutions");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".partitionExecutions");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".jobInstanceExecutions");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutionInstances");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutionStepExecutions");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".latestJobExecutionForInstance");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions");
        clear(InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutionHistory");
    }

    private void clear(final String name) {
        final Cache<?,?> cache = cacheManager.getCache(name, false);
        if (cache == null) {
            return;
        }
        cache.clear();
    }
}
