package io.machinecode.nock.test.core.transport;

import io.machinecode.nock.core.JobOperatorImpl;
import io.machinecode.nock.core.JobOperatorImpl.Start;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.test.core.transport.artifact.batchlet.FailBatchlet;
import io.machinecode.nock.test.core.transport.artifact.batchlet.InjectedBatchlet;
import io.machinecode.nock.test.core.transport.artifact.batchlet.RunBatchlet;
import io.machinecode.nock.test.core.transport.artifact.batchlet.StopBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.CancellationException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class TransportTest extends BaseTest {

    @Test
    public void runBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("run-batchlet")
                                )
                ), PARAMETERS);
        final JobOperatorImpl operator = new JobOperatorImpl(configuration(), transport());
        final Start start = operator.start(job);
        final JobExecution execution = repository().getJobExecution(start.id);
        Assert.assertEquals("Batch Status", BatchStatus.STARTED, execution.getBatchStatus()); //TODO Race
        Assert.assertEquals("Exit  Status", BatchStatus.STARTED.name(), execution.getExitStatus());
        start.deferred.get();
        Assert.assertTrue(RunBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.COMPLETED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.COMPLETED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void stopBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("stop-batchlet")
                                )
                ), PARAMETERS);
        final JobOperatorImpl operator = new JobOperatorImpl(configuration(), transport());
        final Start start = operator.start(job);
        final JobExecution execution = repository().getJobExecution(start.id);
        Assert.assertEquals("Batch Status", BatchStatus.STARTED, execution.getBatchStatus()); //TODO Race
        Assert.assertEquals("Exit  Status", BatchStatus.STARTED.name(), execution.getExitStatus());
        Thread.sleep(100);
        Assert.assertTrue(StopBatchlet.hasRun.get());
        operator.stop(start.id);
        Assert.assertTrue(start.deferred.isCancelled());
        try {
            start.deferred.get();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue(StopBatchlet.hasStopped.get());
        Assert.assertEquals("Batch Status", BatchStatus.STOPPED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.STOPPED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void failBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("fail-batchlet")
                                )
                ), PARAMETERS);
        final JobOperatorImpl operator = new JobOperatorImpl(configuration(), transport());
        final Start start = operator.start(job);
        final JobExecution execution = repository().getJobExecution(start.id);
        Assert.assertEquals("Batch Status", BatchStatus.STARTED, execution.getBatchStatus()); //TODO Race
        Assert.assertEquals("Exit  Status", BatchStatus.STARTED.name(), execution.getExitStatus());
        start.deferred.get();
        Assert.assertTrue(FailBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.FAILED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.FAILED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void injectBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("injected-batchlet")
                                                .addProperty("property", "value")
                                )
                ), PARAMETERS);
        final JobOperatorImpl operator = new JobOperatorImpl(configuration(), transport());
        final Start start = operator.start(job);
        final JobExecution execution = repository().getJobExecution(start.id);
        Assert.assertEquals("Batch Status", BatchStatus.STARTED, execution.getBatchStatus()); //TODO Race
        Assert.assertEquals("Exit  Status", BatchStatus.STARTED.name(), execution.getExitStatus());
        start.deferred.get();
        Assert.assertTrue(InjectedBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.COMPLETED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.COMPLETED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }
}
