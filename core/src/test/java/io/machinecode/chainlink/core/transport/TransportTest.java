package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.base.BaseTest;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.transport.Transport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class TransportTest extends BaseTest {

    protected static TestTransportFactory firstFactory;
    protected static TestTransportFactory secondFactory;
    protected static TestTransportFactory thirdFactory;

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
        if (firstFactory == null) {
            firstFactory = createFactory();
        }
        model.getTransport().setFactory(firstFactory);
    }

    protected void visitSecondJobOperatorModel(final JobOperatorModel model) throws Exception {
        if (secondFactory == null) {
            secondFactory = createFactory();
        }
        model.getTransport().setFactory(secondFactory);
    }

    protected void visitThirdJobOperatorModel(final JobOperatorModel model) throws Exception {
        if (thirdFactory == null) {
            thirdFactory = createFactory();
        }
        model.getTransport().setFactory(thirdFactory);
    }

    protected abstract TestTransportFactory createFactory() throws Exception;

    @Before
    public void before() throws Exception {
        configuration();
        secondConfiguration();
        thirdConfiguration();

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

    @After
    public void after() throws Exception {
        configuration().getTransport().close();
        configuration().getRegistry().close();
        configuration().getExecutor().close();

        secondConfiguration().getTransport().close();
        secondConfiguration().getRegistry().close();
        secondConfiguration().getExecutor().close();

        thirdConfiguration().getTransport().close();
        thirdConfiguration().getRegistry().close();
        thirdConfiguration().getExecutor().close();
    }

    @Test
    public void localAddressTest() throws Exception {
        final Transport transport = configuration().getTransport();
        assertNotNull(transport.getAddress());
    }

    /*
    @Test
    public void getWorkerTest() throws Exception {
        final Transport first = configuration().getTransport();
        final Transport second = secondConfiguration().getTransport();

        final long jobExecutionId = 1;

        final Executable e = new TestExecutable(1, "first", new TestExecutionContext(jobExecutionId));
        configuration().getExecutor().execute(e);
        configuration().getRegistry().registerExecutable(jobExecutionId, e);
        final Promise<RemoteExecution, Throwable, Object> def = second.getWorker(jobExecutionId, e.getId());

        assertNotNull(def.get(1, TimeUnit.SECONDS));
        assertTrue(def.isResolved());
    }

    @Test
    public void getWorkersTest() throws Exception {
        final Transport first = configuration().getTransport();
        final Transport second = secondConfiguration().getTransport();

        final long jobExecutionId = 1;

        final Executable e = new TestExecutable(1, "first", new TestExecutionContext(jobExecutionId));
        configuration().getExecutor().execute(e);
        configuration().getRegistry().registerExecutable(jobExecutionId, e);
        final Promise<List<RemoteExecution>, Throwable, Object> def = second.getWorkers(3).onResolve(new OnResolve<List<RemoteExecution>>() {
            @Override
            public void resolve(final List<RemoteExecution> that) {
                assertEquals(3, that.size());
            }
        });

        assertNotNull(def.get(1, TimeUnit.SECONDS));
        assertTrue(def.isResolved());
    }
    */
}
