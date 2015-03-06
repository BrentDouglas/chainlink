package io.machinecode.chainlink.rt.tomee;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import org.apache.openejb.loader.SystemInstance;

import javax.transaction.TransactionManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class TomEETransactionManagerFactory implements TransactionManagerFactory {
    @Override
    public TransactionManager produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return SystemInstance.get().getComponent(TransactionManager.class);
    }
}
