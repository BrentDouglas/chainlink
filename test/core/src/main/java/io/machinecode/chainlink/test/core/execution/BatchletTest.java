package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.FailBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.InjectedBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.RunBatchlet;
import io.machinecode.chainlink.test.core.execution.artifact.batchlet.StopBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import java.util.concurrent.CancellationException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public abstract class BatchletTest extends OperatorTest {

    @Test
    public void runBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("run-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "run-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("RunBatchlet hasn't run yet", RunBatchlet.hasRun.get());
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void stopBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("stop-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("stopBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "stop-job", PARAMETERS);
        Thread.sleep(100);
        operator.stop(operation.getJobExecutionId());
        //Assert.assertTrue("Operation hasn't been cancelled", operation.isDone()); //Some wierdness in AllChain's done/Cancelled
        try {
            operation.get();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue("StopBatchlet hasn't stopped yet", StopBatchlet.hasStopped.get());
        _assertFinishedWith(BatchStatus.STOPPED, operation.getJobExecutionId());
    }

    @Test
    public void failBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("fail-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("failBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("FailBatchlet hasn't run yet", FailBatchlet.hasRun.get());
        _assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void injectedBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("injected-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("injectedBatchlet")
                                                .addProperty("property", "value")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "injected-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("InjectedBatchlet hasn't run yet", InjectedBatchlet.hasRun.get());
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }
}
