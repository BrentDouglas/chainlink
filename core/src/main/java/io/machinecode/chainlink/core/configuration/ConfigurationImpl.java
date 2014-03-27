package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.InjectionContextImpl;
import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.core.security.SecurityCheckImpl;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.Factory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityCheckFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.security.SecurityCheck;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ConfigurationImpl implements Configuration, RuntimeConfiguration {

    private final ClassLoader classLoader;
    private final ExecutionRepository repository;
    private final TransactionManager transactionManager;
    private final JobLoader jobLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;
    private final SecurityCheck securityCheck;
    private final Properties properties;
    private final Executor executor;
    private final Transport transport;
    private final InjectionContext injectionContext;
    private final WorkerFactory workerFactory;

    protected ConfigurationImpl(final Builder builder) {
        this.properties = builder.properties;
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        this.classLoader = _get(tccl, builder.classLoader, builder.classLoaderFactory, builder.classLoaderFactoryClass, builder.classLoaderFactoryFqcn, tccl, this);
        this.transactionManager = _get(this.classLoader, builder.transactionManager, builder.transactionManagerFactory, builder.transactionManagerFactoryClass, builder.transactionManagerFactoryFqcn, new LocalTransactionManager(180, TimeUnit.SECONDS), this);
        final ArrayList<JobLoader> jobLoaders = _arrayGet(this.classLoader, builder.jobLoaders, builder.jobLoaderFactories, builder.jobLoaderFactoriesClass, builder.jobLoaderFactoriesFqcns, this);
        this.jobLoader = new JobLoaderImpl(this.classLoader, jobLoaders.toArray(new JobLoader[jobLoaders.size()]));
        final ArrayList<ArtifactLoader> artifactLoaders = _arrayGet(this.classLoader, builder.artifactLoaders, builder.artifactLoaderFactories, builder.artifactLoaderFactoriesClass, builder.artifactLoaderFactoriesFqcns, this);
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, artifactLoaders.toArray(new ArtifactLoader[artifactLoaders.size()]));
        final ArrayList<Injector> injectors = _arrayGet(this.classLoader, builder.injectors, builder.injectorFactories, builder.injectorFactoriesClass, builder.injectorFactoriesFqcns, this);
        this.injector = new InjectorImpl(injectors.toArray(new Injector[injectors.size()]));
        final ArrayList<SecurityCheck> securityChecks = _arrayGet(this.classLoader, builder.securityChecks, builder.securityCheckFactories, builder.securityCheckFactoriesClass, builder.securityCheckFactoriesFqcns, this);
        this.securityCheck = new SecurityCheckImpl(securityChecks.toArray(new SecurityCheck[securityChecks.size()]));
        this.injectionContext = new InjectionContextImpl(this.classLoader, this.artifactLoader, this.injector);
        this.repository = _get(this.classLoader, builder.executionRepository, builder.executionRepositoryFactory, builder.executionRepositoryFactoryClass, builder.executionRepositoryFactoryFqcn, null, this);
        this.workerFactory = (WorkerFactory) _getFactory(this.classLoader, builder.workerFactory, builder.workerFactoryClass, builder.workerFactoryFqcn);
        if (this.repository == null) {
            throw new IllegalStateException(); //TODO Message
        }
        this.transport = _get(this.classLoader, builder.transport, builder.transportFactory, builder.transportFactoryClass, builder.transportFactoryFqcn, null, this);
        if (this.transport == null) {
            throw new IllegalStateException(); //TODO Message
        }
        this.executor = _get(this.classLoader, builder.executor, builder.executorFactory, builder.executorFactoryClass, builder.executorFactoryFqcn, null, this);
        if (this.executor == null) {
            throw new IllegalStateException(); //TODO Message
        }
    }

    protected ConfigurationImpl(final XmlConfiguration builder) {
        this.properties = new Properties();
        for (final XmlProperty property : builder.getProperties()) {
            this.properties.put(property.getKey(), property.getValue());
        }
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        this.classLoader = _get(tccl, builder.getClassLoaderFactory().getClazz(), tccl, this);
        this.transactionManager = _get(this.classLoader, builder.getTransactionManagerFactory().getClazz(), new LocalTransactionManager(180, TimeUnit.SECONDS), this);
        final ArrayList<JobLoader> jobLoaders = _get(this.classLoader, builder.getJobLoaderFactories(), this);
        this.jobLoader = new JobLoaderImpl(this.classLoader, jobLoaders.toArray(new JobLoader[jobLoaders.size()]));
        final ArrayList<ArtifactLoader> artifactLoaders = _get(this.classLoader, builder.getArtifactLoaderFactories(), this);
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, artifactLoaders.toArray(new ArtifactLoader[artifactLoaders.size()]));
        final ArrayList<Injector> injectors = _get(this.classLoader, builder.getInjectorFactories(), this);
        this.injector = new InjectorImpl(injectors.toArray(new Injector[injectors.size()]));
        final ArrayList<SecurityCheck> securityChecks = _get(this.classLoader, builder.getSecurityCheckFactories(), this);
        this.securityCheck = new SecurityCheckImpl(securityChecks.toArray(new SecurityCheck[securityChecks.size()]));
        this.injectionContext = new InjectionContextImpl(this.classLoader, this.artifactLoader, this.injector);
        this.repository = _get(this.classLoader, builder.getExecutionRepositoryFactory().getClazz(), null, this);
        this.workerFactory = _getFactory(this.classLoader, builder.getWorkerFactory().getClazz());
        if (this.repository == null) {
            throw new IllegalStateException(); //TODO Message
        }
        this.transport = _get(this.classLoader, builder.getTransportFactory().getClazz(), null, this);
        if (this.transport == null) {
            throw new IllegalStateException(); //TODO Message
        }
        this.executor = _get(this.classLoader, builder.getExecutorFactory().getClazz(), null, this);
        if (this.executor == null) {
            throw new IllegalStateException(); //TODO Message
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    @Override
    public ExecutionRepository getRepository() {
        return this.repository;
    }

    @Override
    public WorkerFactory getWorkerFactory() {
        return workerFactory;
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
    public RuntimeConfiguration getRuntimeConfiguration() {
        return this;
    }

    @Override
    public Transport getTransport() {
        return transport;
    }

    @Override
    public String getProperty(final String key) {
        return properties.getProperty(key);
    }

    @Override
    public String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    @Override
    public Properties getProperties() {
        return properties;
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
    public SecurityCheck getSecurityCheck() {
        return this.securityCheck;
    }

    // Runtime only

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        return transport.getExecutionRepository(id);
    }

    @Override
    public InjectionContext getInjectionContext() {
        return this.injectionContext;
    }

    private <T, U extends BaseConfiguration> T _get(final ClassLoader classLoader, final T that, final Factory<? extends T, U> factory,
                       final Class<? extends Factory<? extends T, U>> clazz, final String fqcn, final T defaultValue, final U configuration) {
        if (that != null) {
            return that;
        }
        if (factory != null) {
            try {
                final T produced =  factory.produce(configuration);
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (clazz != null) {
            try {
                final T produced = clazz.newInstance().produce(configuration);
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (fqcn != null) {
            try {
                final T produced = ((Class<? extends Factory<? extends T, U>>)classLoader.loadClass(fqcn)).newInstance().produce(configuration);
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        return defaultValue;
    }

    private <T, U extends BaseConfiguration> Factory<? extends T, U> _getFactory(final ClassLoader classLoader, final Factory<? extends T, U> factory,
                                                    final Class<? extends Factory<? extends T, U>> clazz, final String fqcn) {
        if (factory != null) {
            try {
                return factory;
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (clazz != null) {
            try {
                final Factory<? extends T, U> produced = clazz.newInstance();
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (fqcn != null) {
            try {
                final Factory<? extends T, U> produced = ((Class<? extends Factory<? extends T, U>>)classLoader.loadClass(fqcn)).newInstance();
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        throw new RuntimeException(); //TODO Message
    }

    private <T, U extends BaseConfiguration> T _get(final ClassLoader classLoader, final String fqcn, final T defaultValue, final U configuration) {
        if (fqcn != null) {
            try {
                final T produced = ((Class<? extends Factory<? extends T, U>>)classLoader.loadClass(fqcn)).newInstance().produce(configuration);
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        return defaultValue;
    }

    private WorkerFactory _getFactory(final ClassLoader classLoader, final String fqcn) {
        if (fqcn != null) {
            try {
                final WorkerFactory produced = ((Class<? extends WorkerFactory>)classLoader.loadClass(fqcn)).newInstance();
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        throw new RuntimeException(); //TODO Message
    }

    private <T, U extends BaseConfiguration> ArrayList<T> _arrayGet(final ClassLoader classLoader, final T[] that, final Factory<? extends T, U>[] factories,
                                                                    final Class<? extends Factory<? extends T, U>>[] clazzes, final String[] fqcns, final U configuration) {
        final ArrayList<T> ret = new ArrayList<T>();
        if (that != null) {
            for (final T t : that) {
                if (t != null) {
                    ret.add(t);
                }
            }
        }
        if (factories != null) {
            for (final Factory<? extends T, U> factory : factories) {
                try {
                    final T produced = factory.produce(configuration);
                    if (produced != null) {
                        ret.add(produced);
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        if (clazzes != null) {
            for (final Class<? extends Factory<? extends T, U>> clazz : clazzes) {
                try {
                    final T produced = clazz.newInstance().produce(configuration);
                    if (produced != null) {
                        ret.add(produced);
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        if (fqcns != null) {
            for (final String fqcn : fqcns) {
                try {
                    final T produced = ((Class<? extends Factory<? extends T, U>>)classLoader.loadClass(fqcn)).newInstance().produce(configuration);
                    if (produced != null) {
                        ret.add(produced);
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        return ret;
    }

    private <T, U extends BaseConfiguration> ArrayList<T> _get(final ClassLoader classLoader, final List<XmlFactoryRef> fqcns, final U configuration) {
        final ArrayList<T> ret = new ArrayList<T>();
        if (fqcns != null) {
            for (final XmlFactoryRef fqcn : fqcns) {
                try {
                    final T produced = ((Class<? extends Factory<? extends T, U>>)classLoader.loadClass(fqcn.getClazz())).newInstance().produce(configuration);
                    if (produced != null) {
                        ret.add(produced);
                    }
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        return ret;
    }

    public static class Builder implements ConfigurationBuilder<Builder> {
        private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        private Executor executor;
        private Transport transport;
        private ExecutionRepository executionRepository;
        private TransactionManager transactionManager;
        private JobLoader[] jobLoaders;
        private ArtifactLoader[] artifactLoaders;
        private Injector[] injectors;
        private SecurityCheck[] securityChecks;
        private Properties properties = new Properties();

        private ExecutorFactory executorFactory;
        private TransportFactory transportFactory;
        private WorkerFactory workerFactory;
        private ClassLoaderFactory classLoaderFactory;
        private ExecutionRepositoryFactory executionRepositoryFactory;
        private TransactionManagerFactory transactionManagerFactory;
        private JobLoaderFactory[] jobLoaderFactories;
        private ArtifactLoaderFactory[] artifactLoaderFactories;
        private InjectorFactory[] injectorFactories;
        private SecurityCheckFactory[] securityCheckFactories;

        private Class<? extends ExecutorFactory> executorFactoryClass;
        private Class<? extends TransportFactory> transportFactoryClass;
        private Class<? extends WorkerFactory> workerFactoryClass;
        private Class<? extends ClassLoaderFactory> classLoaderFactoryClass;
        private Class<? extends ExecutionRepositoryFactory> executionRepositoryFactoryClass;
        private Class<? extends TransactionManagerFactory> transactionManagerFactoryClass;
        private Class<? extends JobLoaderFactory>[] jobLoaderFactoriesClass;
        private Class<? extends ArtifactLoaderFactory>[] artifactLoaderFactoriesClass;
        private Class<? extends InjectorFactory>[] injectorFactoriesClass;
        private Class<? extends SecurityCheckFactory>[] securityCheckFactoriesClass;

        private String executorFactoryFqcn;
        private String transportFactoryFqcn;
        private String workerFactoryFqcn;
        private String classLoaderFactoryFqcn;
        private String executionRepositoryFactoryFqcn;
        private String transactionManagerFactoryFqcn;
        private String[] jobLoaderFactoriesFqcns;
        private String[] artifactLoaderFactoriesFqcns;
        private String[] injectorFactoriesFqcns;
        private String[] securityCheckFactoriesFqcns;

        @Override
        public Builder setClassLoader(final ClassLoader classLoader) {
            this.classLoader = classLoader;
            return this;
        }

        @Override
        public Builder setExecutionRepository(final ExecutionRepository executionRepository) {
            this.executionRepository = executionRepository;
            return this;
        }

        @Override
        public Builder setTransactionManager(final TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
            return this;
        }

        @Override
        public Builder setProperty(final String key, final String value) {
            properties.setProperty(key, value);
            return this;
        }

        @Override
        public Builder setClassLoaderFactory(final ClassLoaderFactory factory) {
            this.classLoaderFactory = factory;
            return this;
        }

        @Override
        public Builder setExecutionRepositoryFactory(final ExecutionRepositoryFactory factory) {
            this.executionRepositoryFactory = factory;
            return this;
        }

        @Override
        public Builder setTransactionManagerFactory(final TransactionManagerFactory factory) {
            this.transactionManagerFactory = factory;
            return this;
        }

        @Override
        public Builder setExecutorFactory(final ExecutorFactory factory) {
            this.executorFactory = factory;
            return this;
        }

        @Override
        public Builder setExecutorFactoryClass(final Class<? extends ExecutorFactory> clazz) {
            this.executorFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setExecutorFactoryFqcn(final String fqcn) {
            this.executorFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setTransport(final Transport transport) {
            this.transport = transport;
            return this;
        }

        @Override
        public Builder setTransportFactory(final TransportFactory factory) {
            this.transportFactory = factory;
            return this;
        }

        @Override
        public Builder setTransportFactoryClass(final Class<? extends TransportFactory> clazz) {
            this.transportFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setTransportFactoryFqcn(final String fqcn) {
            this.transportFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setWorkerFactory(final WorkerFactory factory) {
            this.workerFactory = factory;
            return this;
        }

        @Override
        public Builder setWorkerFactoryClass(final Class<? extends WorkerFactory> clazz) {
            this.workerFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setWorkerFactoryFqcn(final String fqcn) {
            this.workerFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setJobLoaders(final JobLoader... jobLoaders) {
            this.jobLoaders = jobLoaders;
            return this;
        }

        @Override
        public Builder setArtifactLoaders(final ArtifactLoader... artifactLoaders) {
            this.artifactLoaders = artifactLoaders;
            return this;
        }

        @Override
        public Builder setInjectors(final Injector... injectors) {
            this.injectors = injectors;
            return this;
        }

        @Override
        public Builder setSecurityChecks(final SecurityCheck... securityChecks) {
            this.securityChecks = securityChecks;
            return this;
        }

        @Override
        public Builder setJobLoaderFactories(final JobLoaderFactory... jobLoaders) {
            this.jobLoaderFactories = jobLoaders;
            return this;
        }

        @Override
        public Builder setArtifactLoaderFactories(final ArtifactLoaderFactory... factories) {
            this.artifactLoaderFactories = factories;
            return this;
        }

        @Override
        public Builder setInjectorFactories(final InjectorFactory... factories) {
            this.injectorFactories = factories;
            return this;
        }

        @Override
        public Builder setSecurityCheckFactories(final SecurityCheckFactory... factories) {
            this.securityCheckFactories = factories;
            return this;
        }

        @Override
        public Builder setClassLoaderFactoryClass(final Class<? extends ClassLoaderFactory> clazz) {
            this.classLoaderFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setExecutionRepositoryFactoryClass(final Class<? extends ExecutionRepositoryFactory> clazz) {
            this.executionRepositoryFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setTransactionManagerFactoryClass(final Class<? extends TransactionManagerFactory> clazz) {
            this.transactionManagerFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setJobLoaderFactoriesClass(final Class<? extends JobLoaderFactory>... clazzes) {
            this.jobLoaderFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setArtifactLoaderFactoriesClass(final Class<? extends ArtifactLoaderFactory>... clazzes) {
            this.artifactLoaderFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setInjectorFactoriesClass(final Class<? extends InjectorFactory>... clazzes) {
            this.injectorFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setSecurityCheckFactoriesClass(final Class<? extends SecurityCheckFactory>... clazzes) {
            this.securityCheckFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setClassLoaderFactoryFqcn(final String fqcn) {
            this.classLoaderFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setExecutionRepositoryFactoryFqcn(final String fqcn) {
            this.executionRepositoryFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setTransactionManagerFactoryFqcn(final String fqcn) {
            this.transactionManagerFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setExecutor(final Executor executor) {
            this.executor = executor;
            return this;
        }

        @Override
        public Builder setJobLoaderFactoriesFqcns(final String... fqcns) {
            this.jobLoaderFactoriesFqcns = fqcns;
            return this;
        }

        @Override
        public Builder setArtifactLoaderFactoriesFqcns(final String... fqcns) {
            this.artifactLoaderFactoriesFqcns = fqcns;
            return this;
        }

        @Override
        public Builder setInjectorFactoriesFqcns(final String... fqcns) {
            this.injectorFactoriesFqcns = fqcns;
            return this;
        }

        @Override
        public Builder setSecurityCheckFactoriesFqcns(final String... fqcns) {
            this.securityCheckFactoriesFqcns = fqcns;
            return this;
        }

        public ConfigurationImpl build() {
            return new ConfigurationImpl(this);
        }
    }
}
