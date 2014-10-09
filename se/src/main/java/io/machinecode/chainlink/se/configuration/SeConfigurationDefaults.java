package io.machinecode.chainlink.se.configuration;

import io.machinecode.chainlink.core.transport.LocalTransport;
import io.machinecode.chainlink.spi.configuration.ConfigurationDefaults;
import io.machinecode.chainlink.core.execution.EventedExecutor;
import io.machinecode.chainlink.core.execution.EventedWorkerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingProviderFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ExecutorConfiguration;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.ExecutionRepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.WorkerConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class SeConfigurationDefaults implements ConfigurationDefaults {

    final ClassLoader tccl = Thread.currentThread().getContextClassLoader();

    @Override
    public ClassLoader getClassLoader(final Configuration configuration) {
        return tccl;
    }

    @Override
    public TransactionManager getTransactionManager(final LoaderConfiguration configuration) {
        return new LocalTransactionManager(180, TimeUnit.SECONDS);
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryConfiguration configuration) throws Exception {
        return new MemoryExecutionRepository(configuration.getMarshallingProviderFactory().produce(configuration));
    }

    @Override
    public MarshallingProviderFactory getMarshallingProviderFactory(final Configuration configuration) {
        return new JdkMarshallingProviderFactory();
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
    public Transport<?> getTransport(final TransportConfiguration configuration) {
        return new LocalTransport(configuration);
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
