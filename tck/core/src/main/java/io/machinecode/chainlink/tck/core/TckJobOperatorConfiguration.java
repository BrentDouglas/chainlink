package io.machinecode.chainlink.tck.core;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.util.Services;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
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
        final ClassLoader tccl = Tccl.get();

        List<TransactionManagerFactory> transactionManagers = null;
        List<ArtifactLoaderFactory> artifactLoaders = null;
        List<JobLoaderFactory> jobLoaders = null;
        List<SecurityFactory> securitys = null;
        List<MBeanServerFactory> mBeanServer = null;
        List<MarshallingFactory> marshallingFactory = null;

        //These will throw if the properties aren't set but they aren't all required
        try {
            transactionManagers = Services.load(TransactionManagerFactory.class, tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            artifactLoaders = Services.load(ArtifactLoaderFactory.class, tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            jobLoaders = Services.load(JobLoaderFactory.class, tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            securitys = Services.load(SecurityFactory.class, tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            mBeanServer = Services.load(MBeanServerFactory.class, tccl);
        } catch (final ServiceConfigurationError e) {}
        try {
            marshallingFactory = Services.load(MarshallingFactory.class, tccl);
        } catch (final ServiceConfigurationError e) {}
        List<RepositoryFactory> executionRepositories = Services.load(RepositoryFactory.class, tccl);
        List<ExecutorFactory> executors = Services.load(ExecutorFactory.class, tccl);
        List<RegistryFactory> registries = Services.load(RegistryFactory.class, tccl);
        List<TransportFactory> transports = Services.load(TransportFactory.class, tccl);

        model.setProperty(Constants.THREAD_POOL_SIZE, "8");
        model.getTransactionManager().setFactory(transactionManagers == null || transactionManagers.isEmpty() ? null : transactionManagers.get(0));
        if (artifactLoaders != null) {
            for (final ArtifactLoaderFactory factory : artifactLoaders) {
                model.getArtifactLoader(factory.getClass().getSimpleName()).setFactory(factory);
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
        model.getRepository().setFactory(executionRepositories.get(0));
        model.getTransport().setDefaultFactory(transports.get(0));
        model.getExecutor().setFactory(executors.get(0));
        model.getRegistry().setFactory(registries.get(0));
    }
}
