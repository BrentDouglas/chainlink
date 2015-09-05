/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.ehache.test.repository;

import io.machinecode.chainlink.repository.ehcache.EhCacheRepository;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import net.sf.ehcache.CacheManager;
import org.junit.After;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EhCacheRepositoryTest extends RepositoryTest {

    private static final CacheManager manager = new CacheManager();

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = new EhCacheRepository(
                        dependencies.getMarshalling(),
                        manager
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".ids").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobInstances").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".stepExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".partitionExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobInstanceExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutionInstances").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").removeAll();
        manager.getCache(EhCacheRepository.class.getCanonicalName() + ".jobExecutionHistory").removeAll();
    }
}
