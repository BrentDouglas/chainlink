package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.chainlink.core.execution.EventedExecutor;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.execution.Executor;
import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.transaction.TransactionManager;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseTest extends Assert {

    private static final Logger log = Logger.getLogger(BaseTest.class);

    public static final Properties PARAMETERS = new Properties();
    private Executor _executor;
    private ExecutionRepository _repository;
    private RuntimeConfigurationImpl _configuration;
    private TransactionManager _transactionManager;

    protected final RuntimeConfigurationImpl configuration() {
        if (this._configuration == null) {
            this._configuration = new RuntimeConfigurationImpl(_configuration().build());
        }
        return this._configuration;
    }

    protected final ExecutionRepository repository() {
        if (this._repository == null) {
            this._repository = _repository();
        }
        return _repository;
    }

    protected final Executor executor() {
        if (this._executor == null) {
            this._executor = _executor();
        }
        return _executor;
    }

    protected final TransactionManager transactionManager() {
        if (this._transactionManager == null) {
            this._transactionManager = _transactionManager();
        }
        return _transactionManager;
    }

    // Override these for different configurations

    protected Builder _configuration() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setTransactionManager(transactionManager())
                .setRepository(repository());
    }

    protected abstract ExecutionRepository _repository();

    protected final TransactionManager _transactionManager() {
        return new LocalTransactionManager(180, TimeUnit.SECONDS);
    }

    protected Executor _executor() {
        return new EventedExecutor(configuration(), 1);
    }

    protected void printMethodName() {
        log.infof("");
        log.infof("Running test: " + Thread.currentThread().getStackTrace()[2].getMethodName());
        log.infof("");
    }
}
