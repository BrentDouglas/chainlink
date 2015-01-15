package io.machinecode.chainlink.ee.tomee;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import org.apache.openejb.loader.SystemInstance;

import javax.transaction.TransactionManager;
import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class TomEETransactionManagerFactory implements TransactionManagerFactory {
    @Override
    public TransactionManager produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return SystemInstance.get().getComponent(TransactionManager.class);
    }
}
