package io.machinecode.chainlink.spi.configuration;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface SecurityConfiguration extends LoaderConfiguration {

    TransactionManager getTransactionManager();
}
