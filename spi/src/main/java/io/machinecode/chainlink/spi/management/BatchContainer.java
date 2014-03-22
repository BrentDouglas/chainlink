package io.machinecode.chainlink.spi.management;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface BatchContainer {

    Configuration getConfiguration();

    ExtendedJobOperator getJobOperator();

    Executor getExecutor();

    ExecutionRepository getRepository();

    TransactionManager getTransactionManager();
}
