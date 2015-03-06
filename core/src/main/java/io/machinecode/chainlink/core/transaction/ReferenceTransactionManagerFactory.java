package io.machinecode.chainlink.core.transaction;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;

import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ReferenceTransactionManagerFactory implements TransactionManagerFactory {

    final TransactionManager transactionManager;

    public ReferenceTransactionManagerFactory(final TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public TransactionManager produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return transactionManager;
    }
}
