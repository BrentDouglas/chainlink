package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public interface ConfigurationDefaults {

    ClassLoader getClassLoader(final Configuration configuration);

    TransactionManager getTransactionManager(final LoaderConfiguration configuration);

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryConfiguration configuration) throws Exception;

    MarshallingProviderFactory getMarshallingProviderFactory(final Configuration configuration);

    MBeanServer getMBeanServer(final LoaderConfiguration configuration);

    Registry getRegistry(final RegistryConfiguration configuration);

    Transport<?> getTransport(final TransportConfiguration configuration);

    Executor getExecutor(final ExecutorConfiguration configuration);

    WorkerFactory getWorkerFactory(final WorkerConfiguration configuration);
}
