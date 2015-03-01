package io.machinecode.chainlink.core.execution.batchlet;

import io.machinecode.chainlink.core.execution.batchlet.artifact.FailProcessBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.FailStopBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.OverrideBatchlet;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.core.base.OperatorTest;
import io.machinecode.chainlink.core.execution.batchlet.artifact.FailBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.InjectedBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.RunBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.StopBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import static javax.batch.runtime.BatchStatus.COMPLETED;
import static javax.batch.runtime.BatchStatus.FAILED;
import static javax.batch.runtime.BatchStatus.STOPPED;

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
        assertStepFinishedWith(operation, COMPLETED);
        assertJobFinishedWith(operation, COMPLETED);
    }

    @Test
    public void overrideExitStatusBatchletTest() throws Exception {
        printMethodName();
        OverrideBatchlet.reset();
        final Job job = Jsl.job("override-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("overrideBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "override-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("OverrideBatchlet hasn't run yet", OverrideBatchlet.hasRun.get());
        assertStepFinishedWith(operation, COMPLETED, "Step Exit Status");
        assertJobFinishedWith(operation, COMPLETED, "Job Exit Status");
    }

    @Test
    public void stopBatchletTest() throws Exception {
        printMethodName();
        StopBatchlet.reset();
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
        //TODO Is this deterministic? Check this can't end with COMPLETED depending on timing
        assertStepFinishedWith(operation, STOPPED, COMPLETED.name());
        assertJobFinishedWith(operation, STOPPED);
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
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertTrue("FailBatchlet hasn't run yet", FailBatchlet.hasRun.get());
        assertStepFinishedWith(operation, FAILED);
        assertJobFinishedWith(operation, FAILED);
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
        assertStepFinishedWith(operation, COMPLETED);
        assertJobFinishedWith(operation, COMPLETED);
    }

    @Test
    public void failStopBatchletTest() throws Exception {
        printMethodName();
        FailStopBatchlet.reset();
        final Job job = Jsl.job("fail-stop-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("failStopBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-stop-job", PARAMETERS);
        Thread.sleep(100);
        operator.stop(operation.getJobExecutionId());
        try {
            operation.get();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue("FailStopBatchlet hasn't stopped yet", FailStopBatchlet.hasStopped.get());
        //TODO Is this deterministic? Check this can't end with COMPLETED depending on timing
        assertStepFinishedWith(operation, STOPPED, COMPLETED.name());
        assertJobFinishedWith(operation, STOPPED);
    }

    @Test
    public void failProcessBatchletTest() throws Exception {
        printMethodName();
        FailProcessBatchlet.reset();
        final Job job = Jsl.job("fail-process-job")
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("failProcessBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-process-job", PARAMETERS);
        Thread.sleep(100);
        operator.stop(operation.getJobExecutionId());
        try {
            operation.get();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue("FailProcessBatchlet hasn't stopped yet", FailProcessBatchlet.hasStopped.get());
        assertStepFinishedWith(operation, FAILED);
        assertJobFinishedWith(operation, FAILED);
    }
}
