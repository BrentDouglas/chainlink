/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.infinispan.test.repository;

import io.machinecode.chainlink.repository.infinispan.InfinispanRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.jgroups.JChannel;
import org.jgroups.util.Util;
import org.junit.After;

import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanRepositoryTest extends RepositoryTest {

    private EmbeddedCacheManager cacheManager;
    
    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = new InfinispanRepository(
                        dependencies.getMarshalling(),
                        cacheManager = new DefaultCacheManager(
                                new GlobalConfigurationBuilder()
                                        .clusteredDefault()
                                        .transport()
                                        .clusterName("chainlink-test-repository-cluster")
                                        .transport(new JGroupsTransport(new JChannel(Util.getTestStack())))
                                        .globalJmxStatistics()
                                        .jmxDomain("io.machinecode.chainlink.infinispan.test.repository")
                                        .allowDuplicateDomains(true)
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
        clear(InfinispanRepository.class.getCanonicalName() + ".ids");
        clear(InfinispanRepository.class.getCanonicalName() + ".jobInstances");
        clear(InfinispanRepository.class.getCanonicalName() + ".jobExecutions");
        clear(InfinispanRepository.class.getCanonicalName() + ".stepExecutions");
        clear(InfinispanRepository.class.getCanonicalName() + ".partitionExecutions");
        clear(InfinispanRepository.class.getCanonicalName() + ".jobInstanceExecutions");
        clear(InfinispanRepository.class.getCanonicalName() + ".jobExecutionInstances");
        clear(InfinispanRepository.class.getCanonicalName() + ".jobExecutionStepExecutions");
        clear(InfinispanRepository.class.getCanonicalName() + ".latestJobExecutionForInstance");
        clear(InfinispanRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions");
        clear(InfinispanRepository.class.getCanonicalName() + ".jobExecutionHistory");
    }

    private void clear(final String name) {
        final Cache<?,?> cache = cacheManager.getCache(name, false);
        if (cache == null) {
            return;
        }
        cache.clear();
    }
}
