package example;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.marshalling.JdkMarshallingFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryExecutionRepositoryFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;

import javax.batch.runtime.BatchRuntime;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ManualConfiguration {
    public static void main(final String... args) throws Throwable {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).getDeployment(Constants.DEFAULT);
        final JobOperatorModelImpl op = setDefaults(model, tccl);
        configureAndInstall(model, op, tccl);
        BatchRuntime.getJobOperator().start("a_job", new Properties());
    }

    public static JobOperatorModelImpl setDefaults(final DeploymentModelImpl model, final ClassLoader tccl) {
        // Set defaults for the default JobOperator
        final JobOperatorModelImpl op = model.getJobOperator(Constants.DEFAULT);
        op.getClassLoader().setDefaultFactory(new ClassLoaderFactory() {
            @Override
            public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return tccl;
            }
        });
        op.getTransactionManager().setDefaultFactory(new LocalTransactionManagerFactory());
        op.getExecutionRepository().setDefaultFactory(new MemoryExecutionRepositoryFactory());
        op.getMarshalling().setDefaultFactory(new JdkMarshallingFactory());
        op.getTransport().setDefaultFactory(new LocalTransportFactory());
        op.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        op.getExecutor().setDefaultFactory(new EventedExecutorFactory());
        return op;
    }

    public static void configureAndInstall(final DeploymentModelImpl model, final JobOperatorModelImpl op, final ClassLoader tccl) throws Exception {
        // Configure from "chainlink.xml" in root of classpath
        model.loadChainlinkXml();

        final JobOperatorImpl operator = op.createJobOperator();
        Chainlink.setEnvironment(new Environment() {
            @Override
            public JobOperatorImpl getJobOperator(final String name) throws NoConfigurationWithIdException {
                if (Constants.DEFAULT.equals(name)) {
                    return operator;
                }
                throw new NoConfigurationWithIdException("No operator for name " + name);
            }

            @Override
            public Map<String, JobOperatorImpl> getJobOperators() {
                return Collections.singletonMap(Constants.DEFAULT, operator);
            }
        });
    }
}
