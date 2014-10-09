package io.machinecode.chainlink.tck.core;

import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.ConfigurationBuilder;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityCheckFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;

import java.util.List;
import java.util.ServiceConfigurationError;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class TckConfigurator {

    public static <T extends ConfigurationBuilder<T>> T produce(final T builder, final ClassLoader loader) throws Exception {
        List<TransactionManagerFactory> transactionManagers = null;
        List<InjectorFactory> injectors = null;
        List<ArtifactLoaderFactory> artifactLoaders = null;
        List<JobLoaderFactory> jobLoaders = null;
        List<SecurityCheckFactory> securityChecks = null;
        List<MBeanServerFactory> mBeanServer = null;
        List<MarshallingProviderFactory> marshallingProviderFactory = null;

        //These will throw if the properties aren't set but they aren't all required
        try {
            transactionManagers = new ResolvableService<>(TransactionManagerFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        try {
            injectors = new ResolvableService<>(InjectorFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        try {
            artifactLoaders = new ResolvableService<>(ArtifactLoaderFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        try {
            jobLoaders = new ResolvableService<>(JobLoaderFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        try {
            securityChecks = new ResolvableService<>(SecurityCheckFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        try {
            mBeanServer = new ResolvableService<>(MBeanServerFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        try {
            marshallingProviderFactory = new ResolvableService<>(MarshallingProviderFactory.class).resolve(loader);
        } catch (final ServiceConfigurationError e) {}
        List<ExecutionRepositoryFactory> executionRepositories = new ResolvableService<>(ExecutionRepositoryFactory.class).resolve(loader);
        List<ExecutorFactory> executors = new ResolvableService<>(ExecutorFactory.class).resolve(loader);
        List<TransportFactory> transports = new ResolvableService<>(TransportFactory.class).resolve(loader);
        List<RegistryFactory> registries = new ResolvableService<>(RegistryFactory.class).resolve(loader);
        List<WorkerFactory> workers = new ResolvableService<>(WorkerFactory.class).resolve(loader);
        return builder
                .setProperty(Constants.THREAD_POOL_SIZE, "8")
                .setTransactionManagerFactory(transactionManagers == null || transactionManagers.isEmpty() ? null : transactionManagers.get(0))
                .setArtifactLoaderFactories(artifactLoaders == null || artifactLoaders.isEmpty() ? null : artifactLoaders.toArray(new ArtifactLoaderFactory[artifactLoaders.size()]))
                .setInjectorFactories(injectors == null || injectors.isEmpty() ? null : injectors.toArray(new InjectorFactory[injectors.size()]))
                .setSecurityCheckFactories(securityChecks == null || securityChecks.isEmpty() ? null : securityChecks.toArray(new SecurityCheckFactory[securityChecks.size()]))
                .setJobLoaderFactories(jobLoaders == null || jobLoaders.isEmpty() ? null : jobLoaders.toArray(new JobLoaderFactory[jobLoaders.size()]))
                .setMBeanServerFactory(mBeanServer == null || mBeanServer.isEmpty() ? null : mBeanServer.get(0))
                .setMarshallingProviderFactory(marshallingProviderFactory == null || marshallingProviderFactory.isEmpty() ? null : marshallingProviderFactory.get(0))
                .setExecutionRepositoryFactory(executionRepositories.get(0))
                .setExecutorFactory(executors.get(0))
                .setTransportFactory(transports.get(0))
                .setWorkerFactory(workers.get(0))
                .setRegistryFactory(registries.get(0));
    }
}
