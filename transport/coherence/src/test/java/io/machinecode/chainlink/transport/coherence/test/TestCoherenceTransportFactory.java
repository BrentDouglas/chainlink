package io.machinecode.chainlink.transport.coherence.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.coherence.CoherenceTransport;

import java.util.Properties;

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
    public Transport produce(final Dependencies dependencies, final Properties properties) throws Exception {
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
            System.setProperty("tangosol.coherence.cacheconfig", null);
        }
    }

    @Override
    public void close() throws Exception {
        TestCoherenceTransport.doClose(loader);
    }
}
