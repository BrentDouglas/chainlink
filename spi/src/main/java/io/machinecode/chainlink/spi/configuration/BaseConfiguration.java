package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface BaseConfiguration {

    ClassLoader getClassLoader();

    ExecutionRepository getRepository();

    TransactionManager getTransactionManager();
}
