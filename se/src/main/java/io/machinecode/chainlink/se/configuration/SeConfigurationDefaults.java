package io.machinecode.chainlink.se.configuration;

import io.machinecode.chainlink.core.configuration.ConfigurationDefaults;
import io.machinecode.chainlink.core.execution.EventedExecutor;
import io.machinecode.chainlink.core.execution.EventedWorkerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshallerFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
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
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
* @since 1.0
*/
class SeConfigurationDefaults implements ConfigurationDefaults {

    final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

    @Override
    public ClassLoader getClassLoader(final BaseConfiguration configuration) {
        return tccl;
    }

    @Override
    public TransactionManager getTransactionManager(final LoaderConfiguration configuration) {
        return new LocalTransactionManager(180, TimeUnit.SECONDS);
    }

    @Override
    public ExecutionRepository getRepository(final RepositoryConfiguration configuration) throws Exception {
        return new MemoryExecutionRepository(configuration.getMarshallerFactory().produce(configuration));
    }

    @Override
    public MarshallerFactory getMarshallerFactory(final BaseConfiguration configuration) {
        return new JdkMarshallerFactory();
    }

    @Override
    public MBeanServer getMBeanServer(final LoaderConfiguration configuration) {
        return null; //ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public Registry getRegistry(final RegistryConfiguration configuration) {
        return new LocalRegistry();
    }

    @Override
    public Executor getExecutor(final ExecutorConfiguration configuration) {
        return new EventedExecutor(configuration);
    }

    @Override
    public WorkerFactory getWorkerFactory(final WorkerConfiguration configuration) {
        return new EventedWorkerFactory();
    }
}
