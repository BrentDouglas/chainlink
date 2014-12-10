package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
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
    protected Executor _executor;
    protected ExecutionRepository _repository;
    protected ConfigurationImpl _configuration;
    protected TransactionManager _transactionManager;
    protected Marshalling _marshalling;
    protected Registry _registry;

    protected final Configuration configuration() throws Exception {
        if (this._configuration == null) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final SubSystemModelImpl subSystem = new SubSystemModelImpl(tccl);
            final DeploymentModelImpl deployment = subSystem.getDeployment();
            final JobOperatorModelImpl jobOperator = deployment.getJobOperator(Constants.DEFAULT_CONFIGURATION);
            jobOperator.getClassLoader().setDefaultValueFactory(new ClassLoaderFactory() {
                @Override
                public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                    return tccl;
                }
            });
            jobOperator.getTransactionManager().setDefaultValueFactory(new TransactionManagerFactory() {
                @Override
                public TransactionManager produce(final Dependencies dependencies, final Properties properties) throws Exception {
                    return _transactionManager = new LocalTransactionManager(180, TimeUnit.SECONDS);
                }
            });
            jobOperator.getExecutionRepository().setDefaultValueFactory(new MemoryExecutionRepositoryFactory() {
                @Override
                public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                    return _repository = super.produce(dependencies, properties);
                }
            });
            jobOperator.getMarshalling().setDefaultValueFactory(new MarshallingFactory() {
                @Override
                public Marshalling produce(final Dependencies dependencies, final Properties properties) throws Exception {
                    return _marshalling = ((MarshallingFactory) Class.forName(System.getProperty("marshalling.factory.class")).newInstance())
                            .produce(dependencies, properties);
                }
            });
            jobOperator.getTransport().setDefaultValueFactory(new LocalTransportFactory());
            jobOperator.getRegistry().setDefaultValueFactory(new LocalRegistryFactory() {
                @Override
                public LocalRegistry produce(final Dependencies dependencies, final Properties properties) throws Exception {
                    return (LocalRegistry)(_registry = super.produce(dependencies, properties));
                }
            });
            jobOperator.getExecutor().setDefaultValueFactory(new EventedExecutorFactory() {
                @Override
                public Executor produce(final Dependencies dependencies, final Properties properties) {
                    return _executor = super.produce(dependencies, properties);
                }
            });
            this.visitJobOperatorModel(jobOperator);
            this._configuration = deployment.getConfiguration(Constants.DEFAULT_CONFIGURATION);
        }
        return this._configuration;
    }

    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {

    }

    public ExecutionRepository repository() throws Exception {
        configuration();
        return _repository;
    }

    protected void printMethodName() {
        log.infof("");
        log.infof("Running test: %s#%s", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[2].getMethodName());
        log.infof("");
    }
}
