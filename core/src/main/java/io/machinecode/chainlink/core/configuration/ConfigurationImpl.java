package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.InjectionContextImpl;
import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.core.security.SecurityImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.lang.reflect.Array;
import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationImpl implements Configuration {

    protected final ClassLoader classLoader;
    protected final TransactionManager transactionManager;
    protected final Marshalling marshalling;
    protected final MBeanServer mBeanServer;
    protected final JobLoader jobLoader;
    protected final ArtifactLoader artifactLoader;
    protected final Injector injector;
    protected final Security security;
    protected final InjectionContext injectionContext;
    protected final ExecutionRepository executionRepository;
    protected final Transport<?> transport;
    protected final Registry registry;
    protected final Executor executor;

    public ConfigurationImpl(final JobOperatorModelImpl model) throws Exception {
        this.classLoader = nn(model.classLoader);
        this.transactionManager = nn(model.transactionManager);
        this.marshalling = nn(model.marshalling);
        this.mBeanServer = n(model.mBeanServer);
        this.jobLoader = new JobLoaderImpl(this.classLoader, _array(JobLoader.class, model.jobLoaders.values()));
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, _array(ArtifactLoader.class, model.artifactLoaders.values()));
        this.injector = new InjectorImpl(_array(Injector.class, model.injectors.values()));
        this.security = new SecurityImpl(_array(Security.class, model.securities.values()));
        this.injectionContext = new InjectionContextImpl(this.classLoader, this.artifactLoader, this.injector);
        this.registry = nn(model.registry);
        this.transport = nn(model.transport);
        this.executionRepository = nn(model.executionRepository);
        this.executor = nn(model.executor);
    }

    private <T> T n(final DeclarationImpl<T> dec) {
        return dec == null ? null : dec.get(this);
    }

    private <T> T nn(final DeclarationImpl<T> dec) {
        if (dec == null) {
            throw new RuntimeException(); //TODO Message and COnfigurationException
        }
        return dec.get(this);
    }

    private <T> T[] _array(final Class<T> clazz, final Collection<DeclarationImpl<T>> values) {
        @SuppressWarnings("unchecked")
        final T[] ret = (T[])Array.newInstance(clazz, values.size());
        int i = 0;
        for (final DeclarationImpl<T> that : values) {
            ret[i++] = that.get(this);
        }
        return ret;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public ExecutionRepository getExecutionRepository() {
        return this.executionRepository;
    }

    @Override
    public MBeanServer getMBeanServer() {
        return mBeanServer;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        return registry.getExecutionRepository(id);
    }

    @Override
    public JobLoader getJobLoader() {
        return this.jobLoader;
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return this.artifactLoader;
    }

    @Override
    public Injector getInjector() {
        return this.injector;
    }

    @Override
    public Security getSecurity() {
        return this.security;
    }

    @Override
    public Marshalling getMarshalling() {
        return this.marshalling;
    }

    @Override
    public InjectionContext getInjectionContext() {
        return this.injectionContext;
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public Transport<?> getTransport() {
        return transport;
    }
}