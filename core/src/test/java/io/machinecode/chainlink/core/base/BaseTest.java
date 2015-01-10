package io.machinecode.chainlink.core.base;

import io.machinecode.chainlink.core.configuration.ConfigurationArtifactLoader;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.core.transaction.LocalTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
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
            final DeploymentModelImpl deployment = _configure(tccl);
            final JobOperatorModelImpl op = deployment.getJobOperator(Constants.DEFAULT_CONFIGURATION);
            this.visitJobOperatorModel(op);
            this._configuration = deployment.getConfiguration(Constants.DEFAULT_CONFIGURATION);

            final ArtifactLoader loader = new ConfigurationArtifactLoader();
            this._transactionManager = op.getTransactionManager().get(_configuration, loader);
            this._registry = op.getRegistry().get(_configuration, loader);
            this._repository = op.getExecutionRepository().get(_configuration, loader);
            this._marshalling = op.getMarshalling().get(_configuration, loader);
            this._executor = op.getExecutor().get(_configuration, loader);
        }
        return this._configuration;
    }

    protected DeploymentModelImpl _configure(final ClassLoader tccl) {
        final SubSystemModelImpl subSystem = new SubSystemModelImpl(tccl);
        final DeploymentModelImpl deployment = subSystem.getDeployment();
        final JobOperatorModelImpl jobOperator = deployment.getJobOperator(Constants.DEFAULT_CONFIGURATION);
        jobOperator.getClassLoader().setDefaultValueFactory(new ClassLoaderFactory() {
            @Override
            public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return tccl;
            }
        });
        jobOperator.getTransactionManager().setDefaultValueFactory(new LocalTransactionManagerFactory(180, TimeUnit.SECONDS));
        jobOperator.getExecutionRepository().setDefaultValueFactory(new MemoryExecutionRepositoryFactory());
        jobOperator.getMarshalling().setDefaultValueFactory(new MarshallingFactory() {
            @Override
            public Marshalling produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return ((MarshallingFactory) Class.forName(System.getProperty("marshalling.factory.class", JdkMarshallingFactory.class.getName())).newInstance())
                        .produce(dependencies, properties);
            }
        });
        jobOperator.getTransport().setDefaultValueFactory(new LocalTransportFactory());
        jobOperator.getRegistry().setDefaultValueFactory(new LocalRegistryFactory());
        jobOperator.getExecutor().setDefaultValueFactory(new EventedExecutorFactory());
        return deployment;
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
