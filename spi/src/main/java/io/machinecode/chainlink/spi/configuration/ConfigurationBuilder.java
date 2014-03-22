package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.SecurityCheck;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ConfigurationBuilder<T extends ConfigurationBuilder> {

    T setClassLoader(final ClassLoader classLoader);

    T setExecutionRepository(final ExecutionRepository repository);

    T setTransactionManager(final TransactionManager transactionManager);

    T setExecutorFactory(final ExecutorFactory factory);

    T setExecutorFactoryClass(final Class<? extends ExecutorFactory> clazz);

    T setExecutorFactoryFqcn(final String fqcn);

    T setProperty(final String key, final String value);

    T setJobLoaders(final JobLoader... jobLoaders);

    T setArtifactLoaders(final ArtifactLoader... artifactLoaders);

    T setInjectors(final Injector... injectors);

    T setSecurityChecks(final SecurityCheck... securityChecks);

    T setClassLoaderFactory(final Factory<? extends ClassLoader> classLoader);

    T setRepositoryFactory(final Factory<? extends ExecutionRepository> repository);

    T setTransactionManagerFactory(final Factory<? extends TransactionManager> transactionManager);

    T setJobLoaderFactories(final Factory<JobLoader>... factories);

    T setArtifactLoaderFactories(final Factory<ArtifactLoader>... factories);

    T setInjectorFactories(final Factory<Injector>... factories);

    T setSecurityCheckFactories(final Factory<SecurityCheck>... factories);

    T setClassLoaderFactoryClass(final Class<? extends Factory<? extends ClassLoader>> clazz);

    T setRepositoryFactoryClass(final Class<? extends Factory<? extends ExecutionRepository>> clazz);

    T setTransactionManagerFactoryClass(final Class<? extends Factory<? extends TransactionManager>> clazz);

    T setJobLoaderFactoriesClass(final Class<? extends Factory<? extends JobLoader>>... clazzes);

    T setArtifactLoaderFactoriesClass(final Class<? extends Factory<ArtifactLoader>>... clazzes);

    T setInjectorFactoriesClass(final Class<? extends Factory<? extends Injector>>... clazzes);

    T setSecurityCheckFactoriesClass(final Class<? extends Factory<? extends SecurityCheck>>... clazzes);

    T setClassLoaderFactoryFqcn(final String fqcn);

    T setRepositoryFactoryFqcn(final String fqcn);

    T setTransactionManagerFactoryFqcn(final String fqcn);

    T setJobLoaderFactoriesFqcns(final String... fqcns);

    T setArtifactLoaderFactoriesFqcns(final String... fqcns);

    T setInjectorFactoriesFqcns(final String... fqcns);

    T setSecurityCheckFactoriesFqcns(final String... fqcns);
}
