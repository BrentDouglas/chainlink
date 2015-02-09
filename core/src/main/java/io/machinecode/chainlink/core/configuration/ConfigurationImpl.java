package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.InjectionContextImpl;
import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.core.security.SecurityImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Properties;

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
    protected final Repository repository;
    protected final Transport transport;
    protected final Registry registry;
    protected final Executor executor;
    protected final Properties properties;

    public ConfigurationImpl(final JobOperatorModelImpl model, final ArtifactLoader loader) throws Exception {
        this.properties = model.getRawProperties();
        this.classLoader = nn(model.classLoader, loader);
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, _array(ArtifactLoader.class, model.artifactLoaders.values(), loader));
        this.transactionManager = nn(model.transactionManager, loader);
        this.marshalling = nn(model.marshalling, loader);
        this.mBeanServer = n(model.mBeanServer, loader);
        this.jobLoader = new JobLoaderImpl(this.classLoader, _array(JobLoader.class, model.jobLoaders.values(), loader));
        this.injector = new InjectorImpl(_array(Injector.class, model.injectors.values(), loader));
        this.security = new SecurityImpl(_array(Security.class, model.securities.values(), loader));
        this.injectionContext = new InjectionContextImpl(this.classLoader, this.artifactLoader, this.injector);
        this.registry = nn(model.registry, loader);
        this.transport = nn(model.transport, loader);
        this.repository = nn(model.repository, loader);
        this.executor = nn(model.executor, loader);
    }

    private <T> T n(final DeclarationImpl<T> dec, final ArtifactLoader loader) {
        return dec == null ? null : dec.get(this, this.properties, loader);
    }

    private <T> T nn(final DeclarationImpl<T> dec, final ArtifactLoader loader) {
        if (dec == null) {
            throw new ConfigurationException(); //TODO Message
        }
        return dec.get(this, this.properties, loader);
    }

    private <T> T[] _array(final Class<T> clazz, final Collection<DeclarationImpl<T>> values, final ArtifactLoader loader) {
        @SuppressWarnings("unchecked")
        final T[] ret = (T[])Array.newInstance(clazz, values.size());
        int i = 0;
        for (final DeclarationImpl<T> that : values) {
            ret[i++] = that.get(this, this.properties, loader);
        }
        return ret;
    }

    @Override
    public String getProperty(final String name) {
        return this.properties.getProperty(name);
    }

    @Override
    public String getProperty(final String name, final String defaultValue) {
        return this.properties.getProperty(name, defaultValue);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public Repository getRepository() {
        return this.repository;
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
    public Transport getTransport() {
        return transport;
    }
}