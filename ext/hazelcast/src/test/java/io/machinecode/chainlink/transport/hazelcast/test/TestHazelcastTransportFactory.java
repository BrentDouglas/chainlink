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
package io.machinecode.chainlink.transport.hazelcast.test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.transport.hazelcast.HazelcastTransport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestHazelcastTransportFactory implements TestTransportFactory {

    final HazelcastInstance hazelcast;
    final IExecutorService executor;

    public TestHazelcastTransportFactory() {
        this.hazelcast = Hazelcast.newHazelcastInstance();
        this.executor = hazelcast.getExecutorService("chainlink-test-hazelcast-executor");
    }

    @Override
    public HazelcastTransport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new HazelcastTransport(
                dependencies,
                properties,
                this.hazelcast,
                this.executor
        ) {
            @Override
            public void open(final Configuration configuration) throws Exception {
                this.configuration = configuration;
                this.hazelcast.getUserContext().put(Configuration.class.getCanonicalName(), configuration);
            }
        };
    }

    @Override
    public void close() {
        this.executor.shutdown();
        this.hazelcast.shutdown();
    }
}
