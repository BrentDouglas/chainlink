package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface StateContainer {

    RuntimeConfiguration getConfiguration();

    ExtendedJobOperator getOperator();

    Executor getExecutor();

    ExecutionRepository getRepository();

    TransactionManager getTransactionManager();
}
