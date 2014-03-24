package io.machinecode.chainlink.tck.batch;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import io.machinecode.chainlink.spi.configuration.ExecutorFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MemoryBatchConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<ExecutorFactory> factories;
        try {
            factories = new ResolvableService<ExecutorFactory>(ExecutorFactory.class).resolve(tccl);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new Builder()
                .setClassLoader(tccl)
                .setExecutionRepository(new MemoryExecutionRepository(tccl))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setExecutorFactory(factories.get(0))
                .setProperty(Constants.EXECUTOR_THREAD_POOL_SIZE, "8")
                .build();
    }
}
