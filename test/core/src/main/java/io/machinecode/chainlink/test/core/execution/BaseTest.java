package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.se.configuration.SeConfiguration.Builder;
import io.machinecode.chainlink.se.configuration.SeConfiguration;
import io.machinecode.chainlink.se.configuration.SeConfigurationDefaults;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.execution.EventedWorkerFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import org.jboss.logging.Logger;
import org.junit.Assert;

import javax.transaction.TransactionManager;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class BaseTest extends Assert {

    private static final Logger log = Logger.getLogger(BaseTest.class);

    public static final Properties PARAMETERS = new Properties();
    private ExecutorFactory _executor;
    private ExecutionRepository _repository;
    private SeConfiguration _configuration;
    private TransactionManager _transactionManager;
    private MarshallingProviderFactory _marshallingProviderFactory;
    private RegistryFactory _registryFactory;

    protected final SeConfiguration configuration() throws Exception {
        if (this._configuration == null) {
            this._configuration = _configuration()
                    .setConfigurationDefaults(new SeConfigurationDefaults())
                    .build();
        }
        return this._configuration;
    }

    protected final ExecutionRepository repository() throws Exception {
        if (this._repository == null) {
            this._repository = _repository();
        }
        return _repository;
    }

    protected final ExecutorFactory executor() throws Exception{
        if (this._executor == null) {
            this._executor = _executor();
        }
        return _executor;
    }

    protected final TransactionManager transactionManager() throws Exception {
        if (this._transactionManager == null) {
            this._transactionManager = _transactionManager();
        }
        return _transactionManager;
    }

    protected final MarshallingProviderFactory marshallerFactory() throws Exception {
        if (this._marshallingProviderFactory == null) {
            this._marshallingProviderFactory = _marshallerFactory();
        }
        return _marshallingProviderFactory;
    }

    protected final RegistryFactory registryFactory() throws Exception {
        if (this._registryFactory == null) {
            this._registryFactory = _registryFactory();
        }
        return _registryFactory;
    }

    // Override these for different configurations

    protected Builder _configuration() throws Exception {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return new Builder()
                .setClassLoader(tccl)
                .setTransactionManager(transactionManager())
                .setExecutionRepository(repository())
                .setExecutorFactory(executor())
                .setWorkerFactory(_workerFactory())
                .setMarshallingProviderFactory(marshallerFactory())
                .setRegistryFactory(registryFactory())
                .setProperty(Constants.THREAD_POOL_SIZE, "8");
    }

    protected abstract ExecutionRepository _repository() throws Exception;

    protected TransactionManager _transactionManager() throws Exception {
        return new LocalTransactionManager(180, TimeUnit.SECONDS);
    }

    protected MarshallingProviderFactory _marshallerFactory() throws Exception {
        return (MarshallingProviderFactory) Class.forName(System.getProperty("marshalling.provider.factory.class")).newInstance();
    }

    protected ExecutorFactory _executor() throws Exception {
        return new EventedExecutorFactory();
    }

    protected WorkerFactory _workerFactory() throws Exception {
        return new EventedWorkerFactory();
    }

    protected RegistryFactory _registryFactory() throws Exception {
        return new LocalRegistryFactory();
    }

    protected void printMethodName() {
        log.infof("");
        log.infof("Running test: %s#%s", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
        log.infof("");
    }
}
