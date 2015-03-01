package io.machinecode.chainlink.core.execution.batchlet;

import io.machinecode.chainlink.core.base.OperatorTest;
import io.machinecode.chainlink.core.execution.batchlet.artifact.FailBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.FailProcessBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.FailStopBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.InjectedBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.RunBatchlet;
import io.machinecode.chainlink.core.execution.batchlet.artifact.StopBatchlet;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.spi.jsl.Job;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import java.util.concurrent.CancellationException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionedBatchletTest extends OperatorTest {

    @Test
    public void partRunBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("part-run-job")
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "part-run-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("RunBatchlet hasn't run yet", RunBatchlet.hasRun.get());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void partStopBatchletTest() throws Exception {
        printMethodName();
        StopBatchlet.reset();
        final Job job = Jsl.job("stop-job")
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
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
        assertJobFinishedWith(operation, BatchStatus.STOPPED);
    }

    @Test
    public void partFailBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("fail-job")
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
                                .setTask(
                                        Jsl.batchlet("failBatchlet")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("FailBatchlet hasn't run yet", FailBatchlet.hasRun.get());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void partInjectedBatchletTest() throws Exception {
        printMethodName();
        final Job job = Jsl.job("injected-job")
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
                                .setTask(
                                        Jsl.batchlet("injectedBatchlet")
                                                .addProperty("property", "value")
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "injected-job", PARAMETERS);
        operation.get();
        Assert.assertTrue("InjectedBatchlet hasn't run yet", InjectedBatchlet.hasRun.get());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void partFailStopBatchletTest() throws Exception {
        printMethodName();
        FailStopBatchlet.reset();
        final Job job = Jsl.job("fail-stop-job")
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
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
        assertJobFinishedWith(operation, BatchStatus.STOPPED);
    }

    @Test
    public void partFailProcessBatchletTest() throws Exception {
        printMethodName();
        FailProcessBatchlet.reset();
        final Job job = Jsl.job("fail-process-job")
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
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
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }
}
