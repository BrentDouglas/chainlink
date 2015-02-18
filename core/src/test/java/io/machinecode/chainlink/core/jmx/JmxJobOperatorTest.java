package io.machinecode.chainlink.core.jmx;

import io.machinecode.chainlink.core.base.OperatorTest;
import io.machinecode.chainlink.core.execution.batchlet.artifact.StopBatchlet;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.jsl.fluent.execution.FluentStep;
import io.machinecode.chainlink.core.loader.FluentJobLoader;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.management.jmx.JmxJobOperator;
import io.machinecode.chainlink.core.management.jmx.JmxJobOperatorBean;
import io.machinecode.chainlink.core.management.jmx.JmxJobOperatorClient;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import javax.management.MBeanServer;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JmxJobOperatorTest extends OperatorTest {

    final MBeanServer server = ManagementFactory.getPlatformMBeanServer();

    private JmxJobOperatorBean jmx;
    private JmxJobOperator client;
    //first and second should not have mutable tests applied to them
    private long first;
    private long second;
    private long restart;

    @Before
    public void before() throws Exception {
        super.before();
        if (this.jmx == null) {
            this.jmx = new JmxJobOperatorBean(this.operator, configuration());
            this.client = new JmxJobOperator(configuration(), jmx.getName());
            final Properties properties = new Properties();
            properties.setProperty("foo", "bar");
            final JobOperationImpl fo = operator.startJob("first", properties);
            properties.setProperty("bar", "bax");
            final JobOperationImpl so = operator.startJob("second", properties);
            final JobOperationImpl ro = operator.startJob("restart", properties);
            this.first = fo.getJobExecutionId();
            this.second = so.getJobExecutionId();
            this.restart = ro.getJobExecutionId();
            fo.get();
            so.get();
            ro.get();
        }
    }

    @After
    public void after() throws Exception {
        this.jmx.close();
        this.jmx = null;
    }

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getMBeanServer().setValue(server);
        final FluentStep run = Jsl.step("step").setTask(Jsl.batchlet("runBatchlet"));
        final FluentStep stop = Jsl.step("step").setTask(Jsl.batchlet("stopBatchlet"));
        final FluentStep fail = Jsl.step("step").setTask(Jsl.batchlet("failBatchlet"));
        model.getJobLoader("test").setValue(new FluentJobLoader(){{
            add(Jsl.job("first").addExecution(run))
            .add(Jsl.job("second").addExecution(run))
            .add(Jsl.job("restart").addExecution(fail))
            .add(Jsl.job("stop").addExecution(stop))
            .add(Jsl.job("start").addExecution(run))
            .add(Jsl.job("abandon").addExecution(fail));
        }});
    }


    @Test
    public void testGetJobNames() throws Exception {
        final Set<String> set = client.getJobNames();
        assertTrue(set.contains("first"));
        assertTrue(set.contains("second"));
    }

    @Test
    public void testGetJobInstanceCount() throws Exception {
        final int first = client.getJobInstanceCount("first");
        assertEquals(1, first);
        final int second = client.getJobInstanceCount("second");
        assertEquals(1, second);
    }

    @Test
    public void testGetJobInstances() throws Exception {
        final List<JobInstance> first = client.getJobInstances("first", 0, 1);
        assertNotNull(first);
        final List<JobInstance> second = client.getJobInstances("second", 0, 1);
        assertNotNull(second);
    }

    @Test
    public void testGetRunningExecutions() throws Exception {
        final List<Long> first = client.getRunningExecutions("first");
        assertTrue(first.isEmpty());
        StopBatchlet.reset();
        final JobOperationImpl operation = operator.startJob("stop", PARAMETERS);
        final List<Long> stop = client.getRunningExecutions("stop");
        assertEquals(1, stop.size());
        client.stop(operation.getJobExecutionId());
        try {
            operation.get(5, TimeUnit.SECONDS);
        } catch (final CancellationException e) {}
    }

    @Test
    public void testGetParameters() throws Exception {
        final Properties first = client.getParameters(this.first);
        assertNotNull(first);
        final Properties second = client.getParameters(this.second);
        assertNotNull(second);
    }

    @Test
    public void testGetJobInstance() throws Exception {
        final ExtendedJobInstance first = client.getJobInstance(this.first);
        assertNotNull(first);
        final ExtendedJobInstance second = client.getJobInstance(this.second);
        assertNotNull(second);
    }

    @Test
    public void testGetJobExecutions() throws Exception {
        final ExtendedJobInstance fi = operator.getJobInstance(first);
        final ExtendedJobInstance si = operator.getJobInstance(second);
        final List<JobExecution> first = client.getJobExecutions(fi);
        assertNotNull(first);
        final List<JobExecution> second = client.getJobExecutions(si);
        assertNotNull(second);
    }

    @Test
    public void testGetJobExecution() throws Exception {
        final ExtendedJobExecution first = client.getJobExecution(this.first);
        assertNotNull(first);
        final ExtendedJobExecution second = client.getJobExecution(this.second);
        assertNotNull(second);
    }

    @Test
    public void testGetStepExecutions() throws Exception {
        final List<StepExecution> first = client.getStepExecutions(this.first);
        assertNotNull(first);
        final List<StepExecution> second = client.getStepExecutions(this.second);
        assertNotNull(second);
    }

    @Test
    public void testStart() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("asdf","asdf");
        final long next = client.start("start", properties);
        assertNotNull(operator.getJobExecution(next));
    }

    @Test
    public void testRestart() throws Exception {
        final Properties properties = new Properties();
        properties.setProperty("asdf","asdf");
        final long next = client.restart(restart, properties);
        assertNotNull(operator.getJobExecution(next));
    }

    @Test
    public void testStop() throws Exception {
        StopBatchlet.reset();
        final JobOperationImpl operation = operator.startJob("stop", PARAMETERS);
        client.stop(operation.getJobExecutionId());
        try {
            operation.get(5, TimeUnit.SECONDS);
        } catch (final CancellationException e) {}
    }

    @Test
    public void testAbandon() throws Exception {
        final long abandon = operator.startJob("abandon", PARAMETERS)
                .get(10, TimeUnit.SECONDS)
                .getExecutionId();
        client.abandon(abandon);
        assertEquals(BatchStatus.ABANDONED, operator.getJobExecution(abandon).getBatchStatus());
    }
}
