package io.machinecode.chainlink.tck.core;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityCheckFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;

import java.util.List;
import java.util.ServiceConfigurationError;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TckConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        List<TransactionManagerFactory> transactionManagers = null;
        List<InjectorFactory> injectors = null;
        List<ArtifactLoaderFactory> artifactLoaders = null;
        List<JobLoaderFactory> jobLoaders = null;
        List<SecurityCheckFactory> securityChecks = null;
        List<MBeanServerFactory> mBeanServer = null;

        //These will throw if the properties aren't set but they aren't all required
        try {
            transactionManagers = new ResolvableService<TransactionManagerFactory>(TransactionManagerFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            injectors = new ResolvableService<InjectorFactory>(InjectorFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            artifactLoaders = new ResolvableService<ArtifactLoaderFactory>(ArtifactLoaderFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            jobLoaders = new ResolvableService<JobLoaderFactory>(JobLoaderFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            securityChecks = new ResolvableService<SecurityCheckFactory>(SecurityCheckFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            mBeanServer = new ResolvableService<MBeanServerFactory>(MBeanServerFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        List<ExecutionRepositoryFactory> executionRepositories = new ResolvableService<ExecutionRepositoryFactory>(ExecutionRepositoryFactory.class).resolve(tccl);
        List<ExecutorFactory> executors = new ResolvableService<ExecutorFactory>(ExecutorFactory.class).resolve(tccl);
        List<TransportFactory> transports = new ResolvableService<TransportFactory>(TransportFactory.class).resolve(tccl);
        List<WorkerFactory> workers = new ResolvableService<WorkerFactory>(WorkerFactory.class).resolve(tccl);
        return new Builder()
                .setProperty(Constants.THREAD_POOL_SIZE, "8")
                .setClassLoader(tccl)
                .setTransactionManagerFactory(transactionManagers == null ? null : transactionManagers.get(0))
                .setArtifactLoaderFactories(artifactLoaders == null ? null : artifactLoaders.toArray(new ArtifactLoaderFactory[artifactLoaders.size()]))
                .setInjectorFactories(injectors == null ? null : injectors.toArray(new InjectorFactory[injectors.size()]))
                .setSecurityCheckFactories(securityChecks == null ? null : securityChecks.toArray(new SecurityCheckFactory[securityChecks.size()]))
                .setJobLoaderFactories(jobLoaders == null ? null : jobLoaders.toArray(new JobLoaderFactory[jobLoaders.size()]))
                .setMBeanServerFactory(mBeanServer == null || mBeanServer.isEmpty() ? null : mBeanServer.get(0))
                .setExecutionRepositoryFactory(executionRepositories.get(0))
                .setExecutorFactory(executors.get(0))
                .setWorkerFactory(workers.get(0))
                .setTransportFactory(transports.get(0))
                .build();
    }
}