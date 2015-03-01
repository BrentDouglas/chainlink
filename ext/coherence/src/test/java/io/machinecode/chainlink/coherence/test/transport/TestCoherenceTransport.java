package io.machinecode.chainlink.coherence.test.transport;

import com.tangosol.net.CacheFactory;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.core.transport.RemoteExecution;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Promise;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestCoherenceTransport implements Transport {

    final ClassLoader loader;
    final Transport delegate;

    public TestCoherenceTransport(final ClassLoader loader, final Transport delegate) {
        this.loader = loader;
        this.delegate = delegate;
    }

    public static void doClose(final ClassLoader loader) {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            CacheFactory.shutdown();
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public Object getAddress() {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            return delegate.getAddress();
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public Promise<Chain<?>, Throwable, Object> distribute(final int maxThreads, final Executable... executables) throws Exception {

        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            return delegate.distribute(maxThreads, executables);
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public Promise<Chain<?>, Throwable, Object> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception {

        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            return delegate.callback(executableId, context);
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public Repository getRepository(final RepositoryId id) throws Exception {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            return delegate.getRepository(id);
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public long getTimeout() {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            return delegate.getTimeout();
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public TimeUnit getTimeUnit() {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            return delegate.getTimeUnit();
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            delegate.open(configuration);
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }

    @Override
    public void close() throws Exception {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            delegate.close();
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }
}
