package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityCheckFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.SecurityCheck;
import io.machinecode.chainlink.spi.transport.Transport;

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

    T setExecutionRepository(final ExecutionRepository repository);

    T setExecutionRepositoryFactory(final ExecutionRepositoryFactory factory);

    T setExecutionRepositoryFactoryClass(final Class<? extends ExecutionRepositoryFactory> clazz);

    T setExecutionRepositoryFactoryFqcn(final String fqcn);

    T setTransactionManager(final TransactionManager transactionManager);

    T setTransactionManagerFactory(final TransactionManagerFactory transactionManager);

    T setTransactionManagerFactoryClass(final Class<? extends TransactionManagerFactory> clazz);

    T setTransactionManagerFactoryFqcn(final String fqcn);

    T setExecutor(final Executor executor);

    T setExecutorFactory(final ExecutorFactory factory);

    T setExecutorFactoryClass(final Class<? extends ExecutorFactory> clazz);

    T setExecutorFactoryFqcn(final String fqcn);

    T setTransport(final Transport transport);

    T setTransportFactory(final TransportFactory factory);

    T setTransportFactoryClass(final Class<? extends TransportFactory> clazz);

    T setTransportFactoryFqcn(final String fqcn);

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
