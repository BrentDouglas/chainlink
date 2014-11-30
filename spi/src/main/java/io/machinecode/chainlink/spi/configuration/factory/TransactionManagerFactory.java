package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;

import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface TransactionManagerFactory extends Factory<TransactionManager, LoaderConfiguration> {

}
