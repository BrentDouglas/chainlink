package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.core.security.SecurityCheckImpl;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.Factory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.security.SecurityCheck;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ConfigurationImpl implements Configuration {

    private final ClassLoader classLoader;
    private final ExecutionRepository repository;
    private final TransactionManager transactionManager;
    private final JobLoader jobLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;
    private final SecurityCheck securityCheck;
    private final Properties properties;
    private final ExecutorFactory executorFactory;

    protected ConfigurationImpl(final Builder builder) {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        this.classLoader = _get(tccl, builder.classLoader, builder.classLoaderFactory, builder.classLoaderFactoryClass, builder.classLoaderFactoryFqcn, tccl);
        this.repository = _get(this.classLoader, builder.repository, builder.repositoryFactory, builder.repositoryFactoryClass, builder.repositoryFactoryFqcn, null);
        if (this.repository == null) {
            throw new IllegalStateException(); //TODO Message
        }
        this.transactionManager = _get(this.classLoader, builder.transactionManager, builder.transactionManagerFactory, builder.transactionManagerFactoryClass, builder.transactionManagerFactoryFqcn, new LocalTransactionManager(180, TimeUnit.SECONDS));
        final ArrayList<JobLoader> jobLoaders = _get(this.classLoader, builder.jobLoaders, builder.jobLoaderFactories, builder.jobLoaderFactoriesClass, builder.jobLoaderFactoriesFqcns);
        this.jobLoader = new JobLoaderImpl(this.classLoader, jobLoaders.toArray(new JobLoader[jobLoaders.size()]));
        final ArrayList<ArtifactLoader> artifactLoaders = _get(this.classLoader, builder.artifactLoaders, builder.artifactLoaderFactories, builder.artifactLoaderFactoriesClass, builder.artifactLoaderFactoriesFqcns);
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, artifactLoaders.toArray(new ArtifactLoader[artifactLoaders.size()]));
        final ArrayList<Injector> injectors = _get(this.classLoader, builder.injectors, builder.injectorFactories, builder.injectorFactoriesClass, builder.injectorFactoriesFqcns);
        this.injector = new InjectorImpl(injectors.toArray(new Injector[injectors.size()]));
        final ArrayList<SecurityCheck> securityChecks = _get(this.classLoader, builder.securityChecks, builder.securityCheckFactories, builder.securityCheckFactoriesClass, builder.securityCheckFactoriesFqcns);
        this.securityCheck = new SecurityCheckImpl(securityChecks.toArray(new SecurityCheck[securityChecks.size()]));
        this.properties = builder.properties;
        if (builder.executorFactory != null) {
            this.executorFactory = builder.executorFactory;
        } else if (builder.executorFactoryClass != null) {
            try {
                this.executorFactory = builder.executorFactoryClass.newInstance();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        } else if (builder.executorFactoryFqcn != null) {
            try {
                this.executorFactory = ((Class<? extends Factory<? extends ExecutorFactory>>)classLoader.loadClass(builder.executorFactoryFqcn)).newInstance().produce();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        } else {
            throw new IllegalStateException(); //TODO Message
        }
    }

    protected ConfigurationImpl(final XmlConfiguration builder) {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        this.classLoader = _get(tccl, builder.getClassLoaderFactory().getClazz(), tccl);
        this.repository = _get(this.classLoader, builder.getExecutionRepositoryFactory().getClazz(), null);
        if (this.repository == null) {
            throw new IllegalStateException(); //TODO Message
        }
        this.transactionManager = _get(this.classLoader, builder.getTransactionManagerFactory().getClazz(), new LocalTransactionManager(180, TimeUnit.SECONDS));
        final ArrayList<JobLoader> jobLoaders = _get(this.classLoader, builder.getJobLoaderFactories());
        this.jobLoader = new JobLoaderImpl(this.classLoader, jobLoaders.toArray(new JobLoader[jobLoaders.size()]));
        final ArrayList<ArtifactLoader> artifactLoaders = _get(this.classLoader, builder.getArtifactLoaderFactories());
        this.artifactLoader = new ArtifactLoaderImpl(this.classLoader, artifactLoaders.toArray(new ArtifactLoader[artifactLoaders.size()]));
        final ArrayList<Injector> injectors = _get(this.classLoader, builder.getInjectorFactories());
        this.injector = new InjectorImpl(injectors.toArray(new Injector[injectors.size()]));
        final ArrayList<SecurityCheck> securityChecks = _get(this.classLoader, builder.getSecurityCheckFactories());
        this.securityCheck = new SecurityCheckImpl(securityChecks.toArray(new SecurityCheck[securityChecks.size()]));
        this.properties = new Properties();
        for (final XmlProperty property : builder.getProperties()) {
            this.properties.put(property.getKey(), property.getValue());
        }
        if (builder.getExecutionRepositoryFactory() != null) {
            try {
                this.executorFactory = ((Class<? extends Factory<? extends ExecutorFactory>>)classLoader.loadClass(builder.getExecutionRepositoryFactory().getClazz())).newInstance().produce();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        } else {
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
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public ExecutorFactory getExecutorFactory() {
        return executorFactory;
    }

    @Override
    public String getProperty(final String key) {
        return properties.getProperty(key);
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

    private <T> T _get(final ClassLoader classLoader, final T that, final Factory<? extends T> factory,
                       final Class<? extends Factory<? extends T>> clazz, final String fqcn, final T defaultValue) {
        if (that != null) {
            return that;
        }
        if (factory != null) {
            return factory.produce();
        }
        if (clazz != null) {
            try {
                return clazz.newInstance().produce();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        if (fqcn != null) {
            try {
                return ((Class<? extends Factory<? extends T>>)classLoader.loadClass(fqcn)).newInstance().produce();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        return defaultValue;
    }

    private <T> T _get(final ClassLoader classLoader, final String fqcn, final T defaultValue) {
        if (fqcn != null) {
            try {
                return ((Class<? extends Factory<? extends T>>)classLoader.loadClass(fqcn)).newInstance().produce();
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO
            }
        }
        return defaultValue;
    }

    private <T> ArrayList<T> _get(final ClassLoader classLoader, final T[] that, final Factory<? extends T>[] factories,
                                  final Class<? extends Factory<? extends T>>[] clazzes, final String[] fqcns) {
        final ArrayList<T> ret = new ArrayList<T>();
        if (that != null) {
            Collections.addAll(ret, that);
        }
        if (factories != null) {
            for (final Factory<? extends T> factory : factories) {
                ret.add(factory.produce());
            }
        }
        if (clazzes != null) {
            for (final Class<? extends Factory<? extends T>> clazz : clazzes) {
                try {
                    ret.add(clazz.newInstance().produce());
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        if (fqcns != null) {
            for (final String fqcn : fqcns) {
                try {
                    ret.add(((Class<? extends Factory<? extends T>>)classLoader.loadClass(fqcn)).newInstance().produce());
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        return ret;
    }

    private <T> ArrayList<T> _get(final ClassLoader classLoader, final List<XmlFactoryRef> fqcns) {
        final ArrayList<T> ret = new ArrayList<T>();
        if (fqcns != null) {
            for (final XmlFactoryRef fqcn : fqcns) {
                try {
                    ret.add(((Class<? extends Factory<? extends T>>)classLoader.loadClass(fqcn.getClazz())).newInstance().produce());
                } catch (final Exception e) {
                    throw new RuntimeException(e); //TODO
                }
            }
        }
        return ret;
    }

    public static class Builder implements ConfigurationBuilder<Builder> {
        private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        private ExecutionRepository repository;
        private TransactionManager transactionManager;
        private JobLoader[] jobLoaders;
        private ArtifactLoader[] artifactLoaders;
        private Injector[] injectors;
        private SecurityCheck[] securityChecks;
        private Properties properties = new Properties();

        private ExecutorFactory executorFactory;
        private Factory<? extends ClassLoader> classLoaderFactory;
        private Factory<? extends ExecutionRepository> repositoryFactory;
        private Factory<? extends TransactionManager> transactionManagerFactory;
        private Factory<? extends JobLoader>[] jobLoaderFactories;
        private Factory<? extends ArtifactLoader>[] artifactLoaderFactories;
        private Factory<? extends Injector>[] injectorFactories;
        private Factory<? extends SecurityCheck>[] securityCheckFactories;

        private Class<? extends ExecutorFactory> executorFactoryClass;
        private Class<? extends Factory<? extends ClassLoader>> classLoaderFactoryClass;
        private Class<? extends Factory<? extends ExecutionRepository>> repositoryFactoryClass;
        private Class<? extends Factory<? extends TransactionManager>> transactionManagerFactoryClass;
        private Class<? extends Factory<? extends JobLoader>>[] jobLoaderFactoriesClass;
        private Class<? extends Factory<? extends ArtifactLoader>>[] artifactLoaderFactoriesClass;
        private Class<? extends Factory<? extends Injector>>[] injectorFactoriesClass;
        private Class<? extends Factory<? extends SecurityCheck>>[] securityCheckFactoriesClass;

        private String executorFactoryFqcn;
        private String classLoaderFactoryFqcn;
        private String repositoryFactoryFqcn;
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

        public Builder setExecutionRepository(final ExecutionRepository repository) {
            this.repository = repository;
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
        public Builder setClassLoaderFactory(final Factory<? extends ClassLoader> factory) {
            this.classLoaderFactory = factory;
            return this;
        }

        @Override
        public Builder setRepositoryFactory(final Factory<? extends ExecutionRepository> factory) {
            this.repositoryFactory = factory;
            return this;
        }

        @Override
        public Builder setTransactionManagerFactory(final Factory<? extends TransactionManager> factory) {
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
        public Builder setJobLoaderFactories(final Factory<JobLoader>... jobLoaders) {
            this.jobLoaderFactories = jobLoaders;
            return this;
        }

        @Override
        public Builder setArtifactLoaderFactories(final Factory<ArtifactLoader>... factories) {
            this.artifactLoaderFactories = factories;
            return this;
        }

        @Override
        public Builder setInjectorFactories(final Factory<Injector>... factories) {
            this.injectorFactories = factories;
            return this;
        }

        @Override
        public Builder setSecurityCheckFactories(final Factory<SecurityCheck>... factories) {
            this.securityCheckFactories = factories;
            return this;
        }

        @Override
        public Builder setClassLoaderFactoryClass(final Class<? extends Factory<? extends ClassLoader>> clazz) {
            this.classLoaderFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setRepositoryFactoryClass(final Class<? extends Factory<? extends ExecutionRepository>> clazz) {
            this.repositoryFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setTransactionManagerFactoryClass(final Class<? extends Factory<? extends TransactionManager>> clazz) {
            this.transactionManagerFactoryClass = clazz;
            return this;
        }

        @Override
        public Builder setJobLoaderFactoriesClass(final Class<? extends Factory<? extends JobLoader>>... clazzes) {
            this.jobLoaderFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setArtifactLoaderFactoriesClass(final Class<? extends Factory<ArtifactLoader>>... clazzes) {
            this.artifactLoaderFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setInjectorFactoriesClass(final Class<? extends Factory<? extends Injector>>... clazzes) {
            this.injectorFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setSecurityCheckFactoriesClass(final Class<? extends Factory<? extends SecurityCheck>>... clazzes) {
            this.securityCheckFactoriesClass = clazzes;
            return this;
        }

        @Override
        public Builder setClassLoaderFactoryFqcn(final String fqcn) {
            this.classLoaderFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setRepositoryFactoryFqcn(final String fqcn) {
            this.repositoryFactoryFqcn = fqcn;
            return this;
        }

        @Override
        public Builder setTransactionManagerFactoryFqcn(final String fqcn) {
            this.transactionManagerFactoryFqcn = fqcn;
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
