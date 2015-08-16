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
package io.machinecode.chainlink.coherence.test.transport;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.coherence.CoherenceTransport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class TestCoherenceTransportFactory implements TestTransportFactory {

    ClassLoader loader;
    final String config;

    public TestCoherenceTransportFactory(final String config) {
        this.config = config;
    }

    @Override
    public Transport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        loader = TestCoherenceTransportFactory.class.getClassLoader();
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        System.setProperty("tangosol.coherence.cacheconfig", config);
        ct.setContextClassLoader(loader);
        try {
            return new TestCoherenceTransport(
                    loader,
                    new CoherenceTransport(
                            dependencies,
                            properties
                    )
            );
        } finally {
            ct.setContextClassLoader(tccl);
            System.clearProperty("tangosol.coherence.cacheconfig");
        }
    }

    @Override
    public void close() throws Exception {
        TestCoherenceTransport.doClose(loader);
    }
}
