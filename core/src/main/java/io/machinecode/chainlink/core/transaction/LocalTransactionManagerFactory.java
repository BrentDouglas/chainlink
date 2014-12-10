package io.machinecode.chainlink.core.transaction;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;

import javax.transaction.TransactionManager;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class LocalTransactionManagerFactory implements TransactionManagerFactory {

    final long timeout;
    final TimeUnit unit;

    public LocalTransactionManagerFactory(final long timeout, final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public TransactionManager produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new LocalTransactionManager(timeout, unit);
    }
}
