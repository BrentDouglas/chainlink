package io.machinecode.chainlink.core.transaction;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JndiTransactionManagerFactory implements TransactionManagerFactory {

    final String defaultValue;

    public JndiTransactionManagerFactory() {
        this(null);
    }

    public JndiTransactionManagerFactory(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public TransactionManager produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return InitialContext.doLookup(properties.getProperty(Constants.TRANSACTION_MANAGER_JNDI_NAME, this.defaultValue));
    }
}
