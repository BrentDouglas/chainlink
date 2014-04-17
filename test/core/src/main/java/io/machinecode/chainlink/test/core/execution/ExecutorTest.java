package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.FailBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.InjectedBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.RunBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.StopBatchlet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.CancellationException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ExecutorTest extends BaseTest {

    protected JobOperatorImpl operator;

    @Before
    public void before() throws Exception {
        if (operator == null) {
            operator = new JobOperatorImpl(configuration());
            operator.startup();
        }
    }

    @Test
    public void runBatchletTest() throws Exception {
        printMethodName();
        final JobImpl job = JobFactory.produce(Jsl.job()
                .setId("run-job")
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("runBatchlet")
                                )
                ), PARAMETERS);
        final JobOperationImpl operation = operator.startJob(job, "run-job", PARAMETERS);
        final JobExecution execution = repository().getJobExecution(operation.getJobExecutionId());
        operation.get();
        Assert.assertTrue("RunBatchlet hasn't run yet", RunBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.COMPLETED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.COMPLETED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void stopBatchletTest() throws Exception {
        printMethodName();
        final JobImpl job = JobFactory.produce(Jsl.job()
                .setId("stop-job")
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("stopBatchlet")
                                )
                ), PARAMETERS);
        final JobOperationImpl operation = operator.startJob(job, "stop-job", PARAMETERS);
        final JobExecution execution = repository().getJobExecution(operation.getJobExecutionId());
        Thread.sleep(100);
        operator.stop(operation.getJobExecutionId());
        //Assert.assertTrue("Operation hasn't been cancelled", operation.isDone()); //Some wierdness in AllDeferred's done/Cancelled
        try {
            operation.get();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue("StopBatchlet hasn't stopped yet", StopBatchlet.hasStopped.get());
        Assert.assertEquals("Batch Status", BatchStatus.STOPPED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.STOPPED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void failBatchletTest() throws Exception {
        printMethodName();
        final JobImpl job = JobFactory.produce(Jsl.job()
                .setId("fail-job")
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("failBatchlet")
                                )
                ), PARAMETERS);
        final JobOperationImpl operation = operator.startJob(job, "fail-job", PARAMETERS);
        final JobExecution execution = repository().getJobExecution(operation.getJobExecutionId());
        operation.get();
        Assert.assertTrue("FailBatchlet hasn't run yet", FailBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.FAILED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.FAILED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void injectedBatchletTest() throws Exception {
        printMethodName();
        final JobImpl job = JobFactory.produce(Jsl.job()
                .setId("injected-job")
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("injectedBatchlet")
                                                .addProperty("property", "value")
                                )
                ), PARAMETERS);
        final JobOperationImpl operation = operator.startJob(job, "injected-job", PARAMETERS);
        final JobExecution execution = repository().getJobExecution(operation.getJobExecutionId());
        operation.get();
        Assert.assertTrue("InjectedBatchlet hasn't run yet", InjectedBatchlet.hasRun.get());
        Assert.assertEquals("Batch Status", BatchStatus.COMPLETED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals("Exit  Status", BatchStatus.COMPLETED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }
}
