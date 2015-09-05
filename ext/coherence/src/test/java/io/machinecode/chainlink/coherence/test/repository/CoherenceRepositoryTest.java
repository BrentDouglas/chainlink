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
package io.machinecode.chainlink.coherence.test.repository;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.repository.coherence.CoherenceRepository;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.junit.After;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceRepositoryTest extends RepositoryTest {

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        CacheFactory.ensureCluster();
        model.getRepository().setFactory(new RepositoryFactory() {
            @Override
            public Repository produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                return _repository = new CoherenceRepository(
                        dependencies.getMarshalling()
                );
            }
        });
    }

    @After
    public void after() throws Exception {
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".ids").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobInstances").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".stepExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".partitionExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobInstanceExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutionInstances").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutionStepExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".latestJobExecutionForInstance").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions").clear();
        CacheFactory.getCache(CoherenceRepository.class.getCanonicalName() + ".jobExecutionHistory").clear();
    }
}
