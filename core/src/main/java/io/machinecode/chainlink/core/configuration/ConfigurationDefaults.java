package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.WorkerConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.MarshallerFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
* @since 1.0
*/
public interface ConfigurationDefaults {

    ClassLoader getClassLoader(final BaseConfiguration configuration);

    TransactionManager getTransactionManager(final LoaderConfiguration configuration);

    ExecutionRepository getRepository(final RepositoryConfiguration configuration) throws Exception;

    MarshallerFactory getMarshallerFactory(final BaseConfiguration configuration);

    MBeanServer getMBeanServer(final LoaderConfiguration configuration);

    Registry getRegistry(final RegistryConfiguration configuration);

    Executor getExecutor(final ExecutorConfiguration configuration);

    WorkerFactory getWorkerFactory(final WorkerConfiguration configuration);
}
