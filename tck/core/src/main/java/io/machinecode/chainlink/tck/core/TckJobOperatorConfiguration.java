package io.machinecode.chainlink.tck.core;

import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;

import java.util.List;
import java.util.ServiceConfigurationError;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TckJobOperatorConfiguration implements JobOperatorConfiguration {

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

        List<TransactionManagerFactory> transactionManagers = null;
        List<InjectorFactory> injectors = null;
        List<ArtifactLoaderFactory> artifactLoaders = null;
        List<JobLoaderFactory> jobLoaders = null;
        List<SecurityFactory> securitys = null;
        List<MBeanServerFactory> mBeanServer = null;
        List<MarshallingFactory> marshallingFactory = null;

        //These will throw if the properties aren't set but they aren't all required
        try {
            transactionManagers = new ResolvableService<>(TransactionManagerFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            injectors = new ResolvableService<>(InjectorFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            artifactLoaders = new ResolvableService<>(ArtifactLoaderFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            jobLoaders = new ResolvableService<>(JobLoaderFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            securitys = new ResolvableService<>(SecurityFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            mBeanServer = new ResolvableService<>(MBeanServerFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            marshallingFactory = new ResolvableService<>(MarshallingFactory.class).resolve(tccl);
        } catch (final ServiceConfigurationError e) {}
        List<ExecutionRepositoryFactory> executionRepositories = new ResolvableService<>(ExecutionRepositoryFactory.class).resolve(tccl);
        List<ExecutorFactory> executors = new ResolvableService<>(ExecutorFactory.class).resolve(tccl);
        List<RegistryFactory> registries = new ResolvableService<>(RegistryFactory.class).resolve(tccl);
        List<TransportFactory> transports = new ResolvableService<>(TransportFactory.class).resolve(tccl);

        model.getProperties().put(Constants.THREAD_POOL_SIZE, "8");
        model.getTransactionManager().setFactory(transactionManagers == null || transactionManagers.isEmpty() ? null : transactionManagers.get(0));
        if (artifactLoaders != null) {
            for (final ArtifactLoaderFactory factory : artifactLoaders) {
                model.getArtifactLoader(factory.getClass().getSimpleName()).setFactory(factory);
            }
        }
        if (injectors != null) {
            for (final InjectorFactory factory : injectors) {
                model.getInjector(factory.getClass().getSimpleName()).setFactory(factory);
            }
        }
        if (securitys != null) {
            for (final SecurityFactory factory : securitys) {
                model.getSecurity(factory.getClass().getSimpleName()).setFactory(factory);
            }
        }
        if (jobLoaders != null) {
            for (final JobLoaderFactory factory : jobLoaders) {
                model.getJobLoader(factory.getClass().getSimpleName()).setFactory(factory);
            }
        }
        model.getMBeanServer().setFactory(mBeanServer == null || mBeanServer.isEmpty() ? null : mBeanServer.get(0));
        model.getMarshalling().setFactory(marshallingFactory == null || marshallingFactory.isEmpty() ? null : marshallingFactory.get(0));
        model.getExecutionRepository().setFactory(executionRepositories.get(0));
        model.getTransport().setDefaultValueFactory(transports.get(0));
        model.getExecutor().setFactory(executors.get(0));
        model.getRegistry().setFactory(registries.get(0));
    }
}
