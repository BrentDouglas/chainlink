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
package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
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
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanTransportFactory implements TransportFactory {

    @Override
    public InfinispanTransport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        final DefaultCacheManager manager = new DefaultCacheManager(
                new GlobalConfigurationBuilder()
                        .clusteredDefault()
                        .globalJmxStatistics()
                        .jmxDomain("io.machinecode.chainlink.test")
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
