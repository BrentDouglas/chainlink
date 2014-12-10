package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RuntimeConfigurationImpl implements RuntimeConfiguration {

    private final Executor executor;
    private final Registry registry;
    private final Transport<?> transport;
    private final TransactionManager transactionManager;
    private final InjectionContext injectionContext;

    public RuntimeConfigurationImpl(final Executor executor, final Registry registry, final Transport<?> transport,
                                    final TransactionManager transactionManager, final InjectionContext injectionContext) {
        this.executor = executor;
        this.registry = registry;
        this.transport = transport;
        this.transactionManager = transactionManager;
        this.injectionContext = injectionContext;
    }


    @Override
    public Executor getExecutor() {
        return executor;
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public Transport<?> getTransport() {
        return transport;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        return registry.getExecutionRepository(id);
    }

    @Override
    public InjectionContext getInjectionContext() {
        return injectionContext;
    }
}
