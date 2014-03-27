package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RuntimeConfiguration {

    Executor getExecutor();

    Transport getTransport();

    TransactionManager getTransactionManager();

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id);

    InjectionContext getInjectionContext();
}
