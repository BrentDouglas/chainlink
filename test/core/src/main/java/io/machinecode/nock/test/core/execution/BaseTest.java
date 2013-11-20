package io.machinecode.nock.test.core.execution;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.exec.EventedExecutor;
import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.execution.Executor;
import org.jboss.logging.Logger;
import org.junit.Assert;
import sun.reflect.Reflection;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseTest extends Assert {

    private static final Logger log = Logger.getLogger(BaseTest.class);

    public static final Properties PARAMETERS = new Properties();
    private Executor _executor;
    private ExecutionRepository _repository;
    private RuntimeConfigurationImpl _configuration;

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

    protected final Executor transport() {
        if (this._executor == null) {
            this._executor = _transport();
        }
        return _executor;
    }

    // Override these for different configurations

    protected Builder _configuration() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setTransactionManager(new LocalTransactionManager(180))
                .setRepository(repository());
    }

    protected final ExecutionRepository _repository() {
        return new LocalRepository();
    }

    protected final Executor _transport() {
        return new EventedExecutor(configuration(), 1);
    }

    protected void printMethodName() {
        log.info("");
        log.info("Running test: " + Thread.currentThread().getStackTrace()[2].getMethodName());
        log.info("");
    }
}
