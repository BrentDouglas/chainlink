package io.machinecode.chainlink.spi.configuration;

import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface SecurityConfiguration extends LoaderConfiguration {

    TransactionManager getTransactionManager();
}
