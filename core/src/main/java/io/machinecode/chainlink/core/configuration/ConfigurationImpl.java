package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.configuration.xml.XmlClassRef;
import io.machinecode.chainlink.core.configuration.xml.XmlConfiguration;
import io.machinecode.chainlink.core.configuration.xml.XmlProperty;
import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.InjectionContextImpl;
import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.core.security.SecurityCheckImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationDefaults;import io.machinecode.chainlink.spi.configuration.FinalConfiguration;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.Factory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityCheckFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.SecurityCheck;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationImpl implements FinalConfiguration, RuntimeConfiguration {

    protected final ClassLoader classLoader;
    protected final MarshallingProviderFactory marshallingProviderFactory;
    protected final Registry registry;
    protected final ExecutionRepository executionRepository;
    protected final TransactionManager transactionManager;
    protected final JobLoader jobLoader;
    protected final ArtifactLoader artifactLoader;
    protected final Injector injector;
    protected final SecurityCheck securityCheck;
    protected final Properties properties;
    protected final Executor executor;
    protected final InjectionContext injectionContext;
    protected final WorkerFactory workerFactory;
    protected final MBeanServer mBeanServer;

    public ConfigurationImpl(final _Builder<?> builder) throws Exception {
        this.properties = builder.properties;
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        this.classLoader = _get(tccl, builder.classLoader, builder.classLoaderFactory, builder.classLoaderFactoryClass, builder.classLoaderFactoryFqcn, builder.defaults.getClassLoader(this), this);
        this.transactionManager = _get(this.classLoader, builder.transactionManager, builder.transactionManagerFactory, builder.transactionManagerFactoryClass, builder.transactionManagerFactoryFqcn, builder.defaults.getTransactionManager(this), this);
        final ArrayList<JobLoader> jobLoaders = _arrayGet(this.classLoader, builder.jobLoaders, builder.jobLoaderFactories, builder.jobLoaderFactoriesClass, builder.jobLoaderFactoriesFqcns, this);
        this.jobLoader = new JobLoaderImpl(this.classLoader, jobLoaders.toArray(new JobLoader[jobLoaders.size()]));
        final ArrayList<ArtifactLoader> artifactLoaders = _arrayGet(this.classLoader, builder.artifactLoaders, builder.artifactLoaderFactories, builder.artifactLoaderFactoriesClass, builder.artifactLoaderFactoriesFqcns, this);
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, artifactLoaders.toArray(new ArtifactLoader[artifactLoaders.size()]));
        final ArrayList<Injector> injectors = _arrayGet(this.classLoader, builder.injectors, builder.injectorFactories, builder.injectorFactoriesClass, builder.injectorFactoriesFqcns, this);
        this.injector = new InjectorImpl(injectors.toArray(new Injector[injectors.size()]));
        final ArrayList<SecurityCheck> securityChecks = _arrayGet(this.classLoader, builder.securityChecks, builder.securityCheckFactories, builder.securityCheckFactoriesClass, builder.securityCheckFactoriesFqcns, this);
        this.securityCheck = new SecurityCheckImpl(securityChecks.toArray(new SecurityCheck[securityChecks.size()]));
        this.injectionContext = new InjectionContextImpl(this.classLoader, this.artifactLoader, this.injector);
        this.marshallingProviderFactory = (MarshallingProviderFactory) _getFactory(this.classLoader, builder.marshallingProviderFactory, builder.marshallingProviderFactoryClass, builder.marshallingProviderFactoryFqcn, builder.defaults.getMarshallingProviderFactory(this));
        this.executionRepository = _get(this.classLoader, builder.executionRepository, builder.executionRepositoryFactory, builder.executionRepositoryFactoryClass, builder.executionRepositoryFactoryFqcn, builder.defaults.getExecutionRepository(this), this);
        this.mBeanServer = _get(this.classLoader, builder.mBeanServer, builder.mBeanServerFactory, builder.mBeanServerFactoryClass, builder.mBeanServerFactoryFqcn, builder.defaults.getMBeanServer(this), this);
        this.registry = _get(this.classLoader, builder.registry, builder.registryFactory, builder.registryFactoryClass, builder.registryFactoryFqcn, builder.defaults.getRegistry(this), this);
        this.executor = _get(this.classLoader, builder.executor, builder.executorFactory, builder.executorFactoryClass, builder.executorFactoryFqcn, builder.defaults.getExecutor(this), this);
        this.workerFactory = (WorkerFactory) _getFactory(this.classLoader, builder.workerFactory, builder.workerFactoryClass, builder.workerFactoryFqcn, builder.defaults.getWorkerFactory(this));
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
    public WorkerFactory getWorkerFactory() {
        return workerFactory;
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
    public RuntimeConfiguration getRuntimeConfiguration() {
        return this;
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

    @Override
    public MarshallingProviderFactory getMarshallingProviderFactory() {
        return this.marshallingProviderFactory;
    }

    // Runtime only

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        return registry.getExecutionRepository(id);
    }

    @Override
    public InjectionContext getInjectionContext() {
        return this.injectionContext;
    }

    private <T, U extends Configuration> T _get(final ClassLoader classLoader, final T that, final Factory<? extends T, U> factory,
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
                @SuppressWarnings("unchecked")
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

    private <T, U extends Configuration> Factory<? extends T, U> _getFactory(final ClassLoader classLoader, final Factory<? extends T, U> factory,
                                                    final Class<? extends Factory<? extends T, U>> clazz, final String fqcn, Factory<? extends T, U> def) {
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
                @SuppressWarnings("unchecked")
                final Factory<? extends T, U> produced = ((Class<? extends Factory<? extends T, U>>)classLoader.loadClass(fqcn)).newInstance();
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (def != null) {
            return def;
        }
        throw new RuntimeException(); //TODO Message
    }

    private <T> T _getFactory(final ClassLoader classLoader, final String fqcn, final Class<T> clazz, final Class<? extends T> def) {
        if (fqcn != null) {
            try {
                @SuppressWarnings("unchecked")
                final T produced = ((Class<? extends T>)classLoader.loadClass(fqcn)).newInstance();
                if (produced != null) {
                    return produced;
                }
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (def != null) {
            try {
                return def.newInstance();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        throw new RuntimeException(); //TODO Message
    }

    private <T, U extends Configuration> ArrayList<T> _arrayGet(final ClassLoader classLoader, final T[] that, final Factory<? extends T, U>[] factories,
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
                    @SuppressWarnings("unchecked")
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

    @Override
    public Registry getRegistry() {
        return registry;
    }

    public static <T extends _Builder<T>> T configureBuilder(final T builder, final XmlConfiguration xml) {
        for (final XmlProperty property : xml.getProperties()) {
            builder.setProperty(property.getKey(), property.getValue());
        }
        return builder.setClassLoaderFactoryFqcn(xml.getClassLoaderFactory().getClazz())
                .setTransactionManagerFactoryFqcn(xml.getTransactionManagerFactory().getClazz())
                .setJobLoaderFactoriesFqcns(_fqcns(xml.getJobLoaderFactories()))
                .setArtifactLoaderFactoriesFqcns(_fqcns(xml.getArtifactLoaderFactories()))
                .setInjectorFactoriesFqcns(_fqcns(xml.getInjectorFactories()))
                .setSecurityCheckFactoriesFqcns(_fqcns(xml.getSecurityCheckFactories()))
                .setMarshallingProviderFactoryFqcn(xml.getMarshallingProviderFactory().getClazz())
                .setExecutionRepositoryFactoryFqcn(xml.getMarshallingProviderFactory().getClazz())
                .setMBeanServerFactoryFqcn(xml.getmBeanServerFactory().getClazz())
                .setRegistryFactoryFqcn(xml.getRegistryFactory().getClazz())
                .setExecutorFactoryFqcn(xml.getExecutorFactory().getClazz())
                .setWorkerFactoryFqcn(xml.getWorkerFactory().getClazz());
    }

    protected static String[] _fqcns(final List<XmlClassRef> refs) {
        if (refs == null) {
            return null;
        }
        final String[] ret = new String[refs.size()];
        for (int i = 0; i < refs.size(); ++i) {
            ret[i] = refs.get(i).getClazz();
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public abstract static class _Builder<T extends _Builder<T>> implements ConfigurationBuilder<T> {
        private ConfigurationDefaults defaults;

        private ClassLoader classLoader;
        private Executor executor;
        private Registry registry;
        private MBeanServer mBeanServer;
        private ExecutionRepository executionRepository;
        private TransactionManager transactionManager;
        private JobLoader[] jobLoaders;
        private ArtifactLoader[] artifactLoaders;
        private Injector[] injectors;
        private SecurityCheck[] securityChecks;
        private Properties properties = new Properties();

        private ExecutorFactory executorFactory;
        private RegistryFactory registryFactory;
        private MBeanServerFactory mBeanServerFactory;
        private WorkerFactory workerFactory;
        private ClassLoaderFactory classLoaderFactory;
        private MarshallingProviderFactory marshallingProviderFactory;
        private ExecutionRepositoryFactory executionRepositoryFactory;
        private TransactionManagerFactory transactionManagerFactory;
        private JobLoaderFactory[] jobLoaderFactories;
        private ArtifactLoaderFactory[] artifactLoaderFactories;
        private InjectorFactory[] injectorFactories;
        private SecurityCheckFactory[] securityCheckFactories;

        private Class<? extends ExecutorFactory> executorFactoryClass;
        private Class<? extends RegistryFactory> registryFactoryClass;
        private Class<? extends MBeanServerFactory> mBeanServerFactoryClass;
        private Class<? extends WorkerFactory> workerFactoryClass;
        private Class<? extends ClassLoaderFactory> classLoaderFactoryClass;
        private Class<? extends MarshallingProviderFactory> marshallingProviderFactoryClass;
        private Class<? extends ExecutionRepositoryFactory> executionRepositoryFactoryClass;
        private Class<? extends TransactionManagerFactory> transactionManagerFactoryClass;
        private Class<? extends JobLoaderFactory>[] jobLoaderFactoriesClass;
        private Class<? extends ArtifactLoaderFactory>[] artifactLoaderFactoriesClass;
        private Class<? extends InjectorFactory>[] injectorFactoriesClass;
        private Class<? extends SecurityCheckFactory>[] securityCheckFactoriesClass;

        private String executorFactoryFqcn;
        private String registryFactoryFqcn;
        private String mBeanServerFactoryFqcn;
        private String workerFactoryFqcn;
        private String classLoaderFactoryFqcn;
        private String marshallingProviderFactoryFqcn;
        private String executionRepositoryFactoryFqcn;
        private String transactionManagerFactoryFqcn;
        private String[] jobLoaderFactoriesFqcns;
        private String[] artifactLoaderFactoriesFqcns;
        private String[] injectorFactoriesFqcns;
        private String[] securityCheckFactoriesFqcns;

        @Override
        public T setConfigurationDefaults(final ConfigurationDefaults defaults) {
            this.defaults = defaults;
            return (T)this;
        }

        @Override
        public T setClassLoader(final ClassLoader classLoader) {
            this.classLoader = classLoader;
            return (T)this;
        }

        @Override
        public T setExecutionRepository(final ExecutionRepository executionRepository) {
            this.executionRepository = executionRepository;
            return (T)this;
        }

        @Override
        public T setTransactionManager(final TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
            return (T)this;
        }

        @Override
        public T setProperty(final String key, final String value) {
            properties.setProperty(key, value);
            return (T)this;
        }

        @Override
        public T setClassLoaderFactory(final ClassLoaderFactory factory) {
            this.classLoaderFactory = factory;
            return (T)this;
        }

        @Override
        public T setExecutionRepositoryFactory(final ExecutionRepositoryFactory factory) {
            this.executionRepositoryFactory = factory;
            return (T)this;
        }

        @Override
        public T setTransactionManagerFactory(final TransactionManagerFactory factory) {
            this.transactionManagerFactory = factory;
            return (T)this;
        }

        @Override
        public T setExecutorFactory(final ExecutorFactory factory) {
            this.executorFactory = factory;
            return (T)this;
        }

        @Override
        public T setExecutorFactoryClass(final Class<? extends ExecutorFactory> clazz) {
            this.executorFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setExecutorFactoryFqcn(final String fqcn) {
            this.executorFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setMBeanServer(final MBeanServer mBeanServer) {
            this.mBeanServer = mBeanServer;
            return (T)this;
        }

        @Override
        public T setMBeanServerFactory(final MBeanServerFactory factory) {
            this.mBeanServerFactory = factory;
            return (T)this;
        }

        @Override
        public T setMBeanServerFactoryClass(final Class<? extends MBeanServerFactory> clazz) {
            this.mBeanServerFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setMBeanServerFactoryFqcn(final String fqcn) {
            this.mBeanServerFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setWorkerFactory(final WorkerFactory factory) {
            this.workerFactory = factory;
            return (T)this;
        }

        @Override
        public T setWorkerFactoryClass(final Class<? extends WorkerFactory> clazz) {
            this.workerFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setWorkerFactoryFqcn(final String fqcn) {
            this.workerFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setJobLoaders(final JobLoader... jobLoaders) {
            this.jobLoaders = jobLoaders;
            return (T)this;
        }

        @Override
        public T setArtifactLoaders(final ArtifactLoader... artifactLoaders) {
            this.artifactLoaders = artifactLoaders;
            return (T)this;
        }

        @Override
        public T setInjectors(final Injector... injectors) {
            this.injectors = injectors;
            return (T)this;
        }

        @Override
        public T setSecurityChecks(final SecurityCheck... securityChecks) {
            this.securityChecks = securityChecks;
            return (T)this;
        }

        @Override
        public T setJobLoaderFactories(final JobLoaderFactory... jobLoaders) {
            this.jobLoaderFactories = jobLoaders;
            return (T)this;
        }

        @Override
        public T setArtifactLoaderFactories(final ArtifactLoaderFactory... factories) {
            this.artifactLoaderFactories = factories;
            return (T)this;
        }

        @Override
        public T setInjectorFactories(final InjectorFactory... factories) {
            this.injectorFactories = factories;
            return (T)this;
        }

        @Override
        public T setSecurityCheckFactories(final SecurityCheckFactory... factories) {
            this.securityCheckFactories = factories;
            return (T)this;
        }

        @Override
        public T setClassLoaderFactoryClass(final Class<? extends ClassLoaderFactory> clazz) {
            this.classLoaderFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setExecutionRepositoryFactoryClass(final Class<? extends ExecutionRepositoryFactory> clazz) {
            this.executionRepositoryFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setTransactionManagerFactoryClass(final Class<? extends TransactionManagerFactory> clazz) {
            this.transactionManagerFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setJobLoaderFactoriesClass(final Class<? extends JobLoaderFactory>... clazzes) {
            this.jobLoaderFactoriesClass = clazzes;
            return (T)this;
        }

        @Override
        public T setArtifactLoaderFactoriesClass(final Class<? extends ArtifactLoaderFactory>... clazzes) {
            this.artifactLoaderFactoriesClass = clazzes;
            return (T)this;
        }

        @Override
        public T setInjectorFactoriesClass(final Class<? extends InjectorFactory>... clazzes) {
            this.injectorFactoriesClass = clazzes;
            return (T)this;
        }

        @Override
        public T setSecurityCheckFactoriesClass(final Class<? extends SecurityCheckFactory>... clazzes) {
            this.securityCheckFactoriesClass = clazzes;
            return (T)this;
        }

        @Override
        public T setClassLoaderFactoryFqcn(final String fqcn) {
            this.classLoaderFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setMarshallingProviderFactory(final MarshallingProviderFactory marshallingProviderFactory) {
            this.marshallingProviderFactory = marshallingProviderFactory;
            return (T)this;
        }

        @Override
        public T setMarshallingProviderFactoryClass(final Class<? extends MarshallingProviderFactory> clazz) {
            this.marshallingProviderFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setMarshallingProviderFactoryFqcn(final String fqcn) {
            this.marshallingProviderFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setRegistry(final Registry registry) {
            this.registry = registry;
            return (T)this;
        }

        @Override
        public T setRegistryFactory(final RegistryFactory factory) {
            this.registryFactory = factory;
            return (T)this;
        }

        @Override
        public T setRegistryFactoryClass(final Class<? extends RegistryFactory> clazz) {
            this.registryFactoryClass = clazz;
            return (T)this;
        }

        @Override
        public T setRegistryFactoryFqcn(final String fqcn) {
            this.registryFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setExecutionRepositoryFactoryFqcn(final String fqcn) {
            this.executionRepositoryFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setTransactionManagerFactoryFqcn(final String fqcn) {
            this.transactionManagerFactoryFqcn = fqcn;
            return (T)this;
        }

        @Override
        public T setExecutor(final Executor executor) {
            this.executor = executor;
            return (T)this;
        }

        @Override
        public T setJobLoaderFactoriesFqcns(final String... fqcns) {
            this.jobLoaderFactoriesFqcns = fqcns;
            return (T)this;
        }

        @Override
        public T setArtifactLoaderFactoriesFqcns(final String... fqcns) {
            this.artifactLoaderFactoriesFqcns = fqcns;
            return (T)this;
        }

        @Override
        public T setInjectorFactoriesFqcns(final String... fqcns) {
            this.injectorFactoriesFqcns = fqcns;
            return (T)this;
        }

        @Override
        public T setSecurityCheckFactoriesFqcns(final String... fqcns) {
            this.securityCheckFactoriesFqcns = fqcns;
            return (T)this;
        }
    }
}
