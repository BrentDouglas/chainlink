package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityCheckFactory;
import io.machinecode.chainlink.spi.configuration.factory.SerializerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.WhenFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.SecurityCheck;
import io.machinecode.chainlink.spi.then.When;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ConfigurationBuilder<T extends ConfigurationBuilder> {

    T setProperty(final String key, final String value);

    T setClassLoader(final ClassLoader classLoader);

    T setClassLoaderFactory(final ClassLoaderFactory factory);

    T setClassLoaderFactoryClass(final Class<? extends ClassLoaderFactory> clazz);

    T setClassLoaderFactoryFqcn(final String fqcn);

    T setSerializerFactory(final SerializerFactory serializerFactory);

    T setSerializerFactoryClass(final Class<? extends SerializerFactory> clazz);

    T setSerializerFactoryFqcn(final String fqcn);

    T setRegistry(final Registry registry);

    T setRegistryFactory(final RegistryFactory factory);

    T setRegistryFactoryClass(final Class<? extends RegistryFactory> clazz);

    T setRegistryFactoryFqcn(final String fqcn);

    T setExecutionRepository(final ExecutionRepository repository);

    T setExecutionRepositoryFactory(final ExecutionRepositoryFactory factory);

    T setExecutionRepositoryFactoryClass(final Class<? extends ExecutionRepositoryFactory> clazz);

    T setExecutionRepositoryFactoryFqcn(final String fqcn);

    T setTransactionManager(final TransactionManager transactionManager);

    T setTransactionManagerFactory(final TransactionManagerFactory transactionManager);

    T setTransactionManagerFactoryClass(final Class<? extends TransactionManagerFactory> clazz);

    T setTransactionManagerFactoryFqcn(final String fqcn);

    T setWhen(final When when);

    T setWhenFactory(final WhenFactory when);

    T setWhenFactoryClass(final Class<? extends WhenFactory> clazz);

    T setWhenFactoryFqcn(final String fqcn);

    T setExecutor(final Executor executor);

    T setExecutorFactory(final ExecutorFactory factory);

    T setExecutorFactoryClass(final Class<? extends ExecutorFactory> clazz);

    T setExecutorFactoryFqcn(final String fqcn);

    T setMBeanServer(final MBeanServer mBeanServer);

    T setMBeanServerFactory(final MBeanServerFactory factory);

    T setMBeanServerFactoryClass(final Class<? extends MBeanServerFactory> clazz);

    T setMBeanServerFactoryFqcn(final String fqcn);

    T setWorkerFactory(final WorkerFactory factory);

    T setWorkerFactoryClass(final Class<? extends WorkerFactory> clazz);

    T setWorkerFactoryFqcn(final String fqcn);

    T setJobLoaders(final JobLoader... jobLoaders);

    T setJobLoaderFactories(final JobLoaderFactory... factories);

    T setJobLoaderFactoriesClass(final Class<? extends JobLoaderFactory>... clazzes);

    T setJobLoaderFactoriesFqcns(final String... fqcns);

    T setArtifactLoaders(final ArtifactLoader... artifactLoaders);

    T setArtifactLoaderFactories(final ArtifactLoaderFactory... factories);

    T setArtifactLoaderFactoriesClass(final Class<? extends ArtifactLoaderFactory>... clazzes);

    T setArtifactLoaderFactoriesFqcns(final String... fqcns);

    T setInjectors(final Injector... injectors);

    T setInjectorFactories(final InjectorFactory... factories);

    T setInjectorFactoriesClass(final Class<? extends InjectorFactory>... clazzes);

    T setInjectorFactoriesFqcns(final String... fqcns);

    T setSecurityChecks(final SecurityCheck... securityChecks);

    T setSecurityCheckFactories(final SecurityCheckFactory... factories);

    T setSecurityCheckFactoriesClass(final Class<? extends SecurityCheckFactory>... clazzes);

    T setSecurityCheckFactoriesFqcns(final String... fqcns);
}
