package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ClassLoaderDependencies implements Dependencies {

    final WeakReference<ClassLoader> loader;

    public ClassLoaderDependencies(final WeakReference<ClassLoader> loader) {
        this.loader = loader;
    }

    @Override
    public ClassLoader getClassLoader() {
        return loader.get();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return null;
    }

    @Override
    public Marshalling getMarshalling() {
        return null;
    }

    @Override
    public MBeanServer getMBeanServer() {
        return null;
    }

    @Override
    public JobLoader getJobLoader() {
        return null;
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return null;
    }

    @Override
    public Injector getInjector() {
        return null;
    }

    @Override
    public Security getSecurity() {
        return null;
    }

    @Override
    public ExecutionRepository getExecutionRepository() {
        return null;
    }

    @Override
    public Transport getTransport() {
        return null;
    }

    @Override
    public Registry getRegistry() {
        return null;
    }

    @Override
    public InjectionContext getInjectionContext() {
        return null;
    }
}
