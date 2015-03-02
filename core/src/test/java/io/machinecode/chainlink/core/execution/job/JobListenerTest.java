package io.machinecode.chainlink.core.execution.job;

import io.machinecode.chainlink.core.execution.artifact.batchlet.FailBatchlet;
import io.machinecode.chainlink.core.execution.artifact.batchlet.RunBatchlet;
import io.machinecode.chainlink.core.execution.chunk.EventOrderTest;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.listener.CountListener;
import io.machinecode.chainlink.core.execution.artifact.listener.FailAfterJobListener;
import io.machinecode.chainlink.core.execution.artifact.listener.FailBeforeJobListener;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.spi.jsl.Job;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

import java.util.concurrent.ExecutionException;

import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_JOB;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_STEP;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_JOB;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_STEP;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobListenerTest extends EventOrderTest {

    @Test
    public void beforeJobTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        RunBatchlet.reset();
        CountListener.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addListener(Jsl.listener("countListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("runBatchlet")
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("countListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "nothing", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,

                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertEquals(4, CountListener.get());
        assertStepFinishedWith(operation, BatchStatus.COMPLETED);
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void beforeJobThrowTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        FailBatchlet.reset();
        FailBeforeJobListener.reset();
        CountListener.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addListener(Jsl.listener("failBeforeJobListener"))
                .addListener(Jsl.listener("failBeforeJobListener"))
                .addListener(Jsl.listener("countListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("failBatchlet")
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("countListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-before", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB
        }, EventOrderAccumulator.order());
        assertEquals(1, CountListener.get());
        assertEquals(2, FailBeforeJobListener.get());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void afterJobThrowTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        FailBatchlet.reset();
        FailAfterJobListener.reset();
        CountListener.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addListener(Jsl.listener("failAfterJobListener"))
                .addListener(Jsl.listener("failAfterJobListener"))
                .addListener(Jsl.listener("countListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.batchlet("failBatchlet")
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("countListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,

                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertEquals(4, CountListener.get());
        assertEquals(2, FailAfterJobListener.get());
        assertStepFinishedWith(operation, BatchStatus.FAILED);
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }
}
