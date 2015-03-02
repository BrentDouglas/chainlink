package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.reader.StopEventOrderReader;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.then.api.Promise;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

import java.util.concurrent.CancellationException;

import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_CHUNK;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_JOB;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_PROCESS;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_READ;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_STEP;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.AFTER_WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_CHUNK;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_JOB;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_PROCESS;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_READ;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_STEP;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEFORE_WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.BEGIN_TRANSACTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.COMMIT_TRANSACTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.PROCESS;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READ;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READER_CLOSE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READER_OPEN;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionedChunkTest extends EventOrderTest {

    @Test
    public void noItemChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("neverEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "no-item", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void oneItemChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "one-item", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void stopChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        StopEventOrderReader.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("stopEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "stop-one-item", PARAMETERS);
        operator.stop(operation.getJobExecutionId());
        StopEventOrderReader.await();
        Thread.sleep(100);
        StopEventOrderReader.go();
        try {
            operation.get();
        } catch (final CancellationException e) {}
        Assert.assertTrue("StopEventOrderReader hasn't stopped yet", StopEventOrderReader.hasStopped.get());
        assertJobFinishedWith(operation, BatchStatus.STOPPED);
    }

    @Test
    public void collectThrowsChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                        .setCollector(Jsl.collector("testCollector"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("stopEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "collect-one-item", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void everythingChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition()
                                        .setStrategy(Jsl.mapper("testMapper"))
                                        .setAnalyzer(Jsl.analyser("testAnalyzer"))
                                        .setCollector(Jsl.collector("testCollector"))
                                        .setReducer(Jsl.reducer("testReducer"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "everything", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void notEnoughPropsChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition()
                                        .setStrategy(Jsl.mapper("notEnoughPropsTestMapper"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "not-enough-props", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void analyzerNoCollectorChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition()
                                        .setStrategy(Jsl.plan()
                                                .setPartitions(2)
                                                .setThreads(2))
                                        .setAnalyzer(Jsl.analyser("testAnalyzer"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "analyzer-no-collector", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void analyzerNoItemsChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition()
                                        .setStrategy(Jsl.plan()
                                                .setPartitions(2)
                                                .setThreads(2))
                                        .setAnalyzer(Jsl.analyser("testAnalyzer"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("alwaysEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "analyzer-no-items", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void failAnalyzeDataChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition()
                                        .setStrategy(Jsl.plan()
                                                .setPartitions(2)
                                                .setThreads(2))
                                        .setAnalyzer(Jsl.analyser("failDataTestAnalyzer"))
                                        .setCollector(Jsl.collector("testCollector"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-analyze-data", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAnalyzeStatusChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition()
                                        .setStrategy(Jsl.plan()
                                                .setPartitions(2)
                                                .setThreads(2))
                                        .setAnalyzer(Jsl.analyser("failStatusTestAnalyzer"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("oneEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-analyze-data", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }
}
