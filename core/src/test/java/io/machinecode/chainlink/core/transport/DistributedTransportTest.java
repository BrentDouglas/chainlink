package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.transport.artifacts.TestId;
import io.machinecode.chainlink.core.transport.artifacts.TestExecutable;
import io.machinecode.chainlink.core.transport.artifacts.TestTransport;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Transport;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DistributedTransportTest extends TransportTest {

    private static final ConcurrentMap<String, TestTransport> transports = new ConcurrentHashMap<>();
    protected ConfigurationImpl _secondConfiguration;
    protected ConfigurationImpl _thirdConfiguration;

    protected final Configuration secondConfiguration() throws Exception {
        if (this._secondConfiguration == null) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final DeploymentModelImpl deployment = _configure(tccl);
            visitSecondJobOperatorModel(deployment.getJobOperator(Constants.DEFAULT));
            this._secondConfiguration = deployment.getConfiguration(Constants.DEFAULT);
        }
        return this._secondConfiguration;
    }

    protected final Configuration thirdConfiguration() throws Exception {
        if (this._thirdConfiguration == null) {
            final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            final DeploymentModelImpl deployment = _configure(tccl);
            visitThirdJobOperatorModel(deployment.getJobOperator(Constants.DEFAULT));
            this._thirdConfiguration = deployment.getConfiguration(Constants.DEFAULT);
        }
        return this._thirdConfiguration;
    }

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport<?> produce(final Dependencies dependencies, final java.util.Properties properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "first", Arrays.asList("second", "third"), dependencies, properties);
                transports.put("first", transport);
                return transport;
            }
        });
    }

    protected void visitSecondJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport<?> produce(final Dependencies dependencies, final Properties properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "second", Arrays.asList("first", "third"), dependencies, properties);
                transports.put("second", transport);
                return transport;
            }
        });
    }

    protected void visitThirdJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport<?> produce(final Dependencies dependencies, final Properties properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "third", Arrays.asList("first", "second"), dependencies, properties);
                transports.put("third", transport);
                return transport;
            }
        });
    }

    @Before
    public void before() throws Exception {
        configuration();
        secondConfiguration();
        thirdConfiguration();
        assertEquals(3, transports.size());

        configuration().getTransport().open(_configuration);
        configuration().getRegistry().open(_configuration);
        configuration().getExecutor().open(_configuration);

        secondConfiguration().getTransport().open(_secondConfiguration);
        secondConfiguration().getRegistry().open(_secondConfiguration);
        secondConfiguration().getExecutor().open(_secondConfiguration);

        thirdConfiguration().getTransport().open(_thirdConfiguration);
        thirdConfiguration().getRegistry().open(_thirdConfiguration);
        thirdConfiguration().getExecutor().open(_thirdConfiguration);
    }

    @Test
    public void localAddressTest() throws Exception {
        final Transport<?> transport = configuration().getTransport();
        assertNotNull(transport.getAddress());
    }

    @Test
    public void getExecutableTest() throws Exception {
        final Transport<?> first = configuration().getTransport();
        final Transport<?> second = secondConfiguration().getTransport();

        configuration().getRegistry().registerExecutable(1, new TestExecutable(1, "first"));

        final Executable executable = second.getExecutable(1, new TestId(1, "first"));
        assertNotNull(executable);
        assertEquals(new TestId(1, "first"), executable.getId());
    }

    @Test
    public void getExecutionRepositoryTest() throws Exception {
        final Transport<?> first = configuration().getTransport();
        final Transport<?> second = secondConfiguration().getTransport();

        final ExecutionRepository in = configuration().getExecutionRepository();
        final ExecutionRepositoryId id = first.generateExecutionRepositoryId();
        configuration().getRegistry().registerExecutionRepository(id, in);

        final ExecutionRepository out = second.getExecutionRepository(id);
        assertNotNull(out);
        assertNotSame(in, out);
    }

    @Test
    public void getWorkerTest() throws Exception {
        final Transport<?> first = configuration().getTransport();
        final Transport<?> second = secondConfiguration().getTransport();

        final Worker in = first.getWorker();

        final Worker out = second.getWorker(in.id());
        assertNotNull(out);
        assertNotSame(in, out);
    }
}
