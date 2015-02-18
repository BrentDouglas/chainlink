package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.base.BaseTest;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.jsl.fluent.task.FluentBatchlet;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.transport.Transport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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

    protected JobOperatorImpl firstOperator;
    protected JobOperatorImpl secondOperator;
    protected JobOperatorImpl thirdOperator;

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
        this.firstOperator = new JobOperatorImpl(configuration());
        this.firstOperator.open(configuration());
        this.secondOperator = new JobOperatorImpl(secondConfiguration());
        this.secondOperator.open(secondConfiguration());
        this.thirdOperator = new JobOperatorImpl(thirdConfiguration());
        this.thirdOperator.open(thirdConfiguration());
    }

    @After
    public void after() throws Exception {
        this.firstOperator.close();
        this.secondOperator.close();
        this.thirdOperator.close();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        if (firstFactory != null) {
            firstFactory.close();
        }
        if (secondFactory != null) {
            secondFactory.close();
        }
        if (thirdFactory != null) {
            thirdFactory.close();
        }
    }

    @Test
    public void localAddressTest() throws Exception {
        final Transport transport = configuration().getTransport();
        assertNotNull(transport.getAddress());
    }

    @Test
    public void splitTest() throws Exception {
        final FluentBatchlet batchlet = Jsl.batchlet("runBatchlet");
        final JobOperationImpl op = firstOperator.startJob(Jsl.job("split-job").addExecution(
                Jsl.split("split")
                        .addFlow(Jsl.flow("f1")
                                .addExecution(Jsl.step("s1").setBatchlet(batchlet)))
                        .addFlow(Jsl.flow("f2")
                                .addExecution(Jsl.step("s2").setBatchlet(batchlet)))
                        .addFlow(Jsl.flow("f3")
                                .addExecution(Jsl.step("s3").setBatchlet(batchlet)))
        ), "split-job", new Properties());

        final JobExecution ex = op.get(5, TimeUnit.SECONDS);
        assertNotNull(ex);
        assertEquals(BatchStatus.COMPLETED, ex.getBatchStatus());
    }

    @Test
    public void partitionedStepTest() throws Exception {
        final JobOperationImpl op = firstOperator.startJob(Jsl.job("partitioned-step-job").addExecution(
                Jsl.step("step")
                        .setPartition(Jsl.partition()
                                .setPlan(Jsl.plan()
                                        .setPartitions("4")
                                        .setThreads("4"))
                        )
                        .setBatchlet(Jsl.batchlet("runBatchlet"))
        ), "partitioned-step-job", new Properties());

        final JobExecution ex = op.get(5, TimeUnit.SECONDS);
        assertNotNull(ex);
        assertEquals(BatchStatus.COMPLETED, ex.getBatchStatus());
    }
}
