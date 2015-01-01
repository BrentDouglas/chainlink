package io.machinecode.chainlink.core.execution.batchlet;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.core.base.OperatorTest;
import io.machinecode.chainlink.core.execution.batchlet.artifact.FailBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.InjectedBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.RunBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.StopBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import java.util.concurrent.CancellationException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchletTest extends OperatorTest {

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
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
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
        try {
            operation.get();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue("StopBatchlet hasn't stopped yet", StopBatchlet.hasStopped.get());
        assertFinishedWith(BatchStatus.STOPPED, operation.getJobExecutionId());
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
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
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
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }
}
