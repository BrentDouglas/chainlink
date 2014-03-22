package io.machinecode.chainlink.tck.batch;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MemoryBatchConfigurationFactory implements ConfigurationFactory {

    @Override
    public Configuration produce() {
        return new Builder()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setExecutionRepository(new MemoryExecutionRepository())
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setExecutorFactoryClass(EventedExecutorFactory.class)
                .setProperty(Constants.EXECUTOR_THREAD_POOL_SIZE, "8")
                .build();
    }
}
