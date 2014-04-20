package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.repository.core.JdkSerializerFactory;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.execution.EventedWorkerFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.SerializerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.configuration.factory.WorkerFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
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
    private ExecutorFactory _executor;
    private ExecutionRepository _repository;
    private ConfigurationImpl _configuration;
    private TransactionManager _transactionManager;
    private SerializerFactory _serializerFactory;

    protected final ConfigurationImpl configuration() throws Exception {
        if (this._configuration == null) {
            this._configuration = _configuration().build();
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

    protected final TransactionManager transactionManager() {
        if (this._transactionManager == null) {
            this._transactionManager = _transactionManager();
        }
        return _transactionManager;
    }

    protected final SerializerFactory serializerFactory() {
        if (this._serializerFactory == null) {
            this._serializerFactory = _serializerFactory();
        }
        return _serializerFactory;
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
                .setTransportFactory(_transportFactory())
                .setSerializerFactory(serializerFactory())
                .setProperty(Constants.THREAD_POOL_SIZE, "8");
    }

    protected abstract ExecutionRepository _repository() throws Exception;

    protected final TransactionManager _transactionManager() {
        return new LocalTransactionManager(180, TimeUnit.SECONDS);
    }

    protected SerializerFactory _serializerFactory() {
        return new JdkSerializerFactory();
    }

    protected ExecutorFactory _executor() throws Exception{
        return new EventedExecutorFactory();
    }

    protected TransportFactory _transportFactory() {
        return new LocalTransportFactory();
    }

    protected WorkerFactory _workerFactory() {
        return new EventedWorkerFactory();
    }

    protected void printMethodName() {
        log.infof("");
        log.infof("Running test: " + Thread.currentThread().getStackTrace()[2].getMethodName());
        log.infof("");
    }
}
