package io.machinecode.nock.test.core.transport;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.core.local.LocalTransport;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.transport.Transport;
import org.junit.Assert;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseTest extends Assert {

    public static final Properties PARAMETERS = new Properties();
    private Transport _transport;
    private Repository _repository;
    private RuntimeConfigurationImpl _configuration;

    protected final RuntimeConfigurationImpl configuration() {
        if (this._configuration == null) {
            this._configuration = new RuntimeConfigurationImpl(_configuration().build());
        }
        return this._configuration;
    }

    protected final Repository repository() {
        if (this._repository == null) {
            this._repository = _repository();
        }
        return _repository;
    }

    protected final Transport transport() {
        if (this._transport == null) {
            this._transport = _transport();
        }
        return _transport;
    }

    // Override these for different configurations

    protected Builder _configuration() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setTransactionManager(new LocalTransactionManager(180))
                .setRepository(repository());
    }

    protected final Repository _repository() {
        return new LocalRepository();
    }

    protected final Transport _transport() {
        return new LocalTransport(configuration(), 1);
    }
}
