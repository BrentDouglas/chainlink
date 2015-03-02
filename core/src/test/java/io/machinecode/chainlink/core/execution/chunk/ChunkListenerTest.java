package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.spi.jsl.Job;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

import java.util.concurrent.ExecutionException;

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
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ON_CHUNK_ERROR;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ON_PROCESS_ERROR;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ON_READ_ERROR;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ON_WRITE_ERROR;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.PROCESS;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READ;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READER_CLOSE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.READER_OPEN;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChunkListenerTest extends EventOrderTest {

    @Test
    public void failBeforeChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("neverEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failBeforeChunkListener"))
                                .addListener(Jsl.listener("failBeforeChunkListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-before-chunk", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK /* throws */,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failOnErrorTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failOnErrorListener"))
                                .addListener(Jsl.listener("failOnErrorListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-on-error", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR /* throws */,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAfterChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("neverEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failAfterChunkListener"))
                                .addListener(Jsl.listener("failAfterChunkListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after-chunk", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                AFTER_CHUNK /* throws */,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    // Read

    @Test
    public void failBeforeReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("neverEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failBeforeReadListener"))
                                .addListener(Jsl.listener("failBeforeReadListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-before-read", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failOnReadErrorTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failOnReadErrorListener"))
                                .addListener(Jsl.listener("failOnReadErrorListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-on-read-error", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR /* throws */,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAfterReadNoItemChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("neverEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failAfterReadListener"))
                                .addListener(Jsl.listener("failAfterReadListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after-read", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAfterReadItemChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failAfterReadListener"))
                                .addListener(Jsl.listener("failAfterReadListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after-read", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    // Process

    @Test
    public void failBeforeProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failBeforeProcessListener"))
                                .addListener(Jsl.listener("failBeforeProcessListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-before-process", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS /* throws */, ON_PROCESS_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failOnProcessErrorTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("failEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failOnProcessErrorListener"))
                                .addListener(Jsl.listener("failOnProcessErrorListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-on-process-error", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR /* throws */,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAfterProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failAfterProcessListener"))
                                .addListener(Jsl.listener("failAfterProcessListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after-process", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS /* throws */, ON_PROCESS_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAfterProcessFilteredChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("alwaysEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failAfterProcessListener"))
                                .addListener(Jsl.listener("failAfterProcessListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after-process-filtered", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS /* throws */, ON_PROCESS_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }


    // Write

    @Test
    public void failBeforeWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failBeforeWriteListener"))
                                .addListener(Jsl.listener("failBeforeWriteListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-before-write", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE /* throws */, ON_WRITE_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failOnWriteErrorTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("failWriteEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failOnWriteErrorListener"))
                                .addListener(Jsl.listener("failOnWriteErrorListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-on-write-error", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR /* throws */,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void failAfterWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failAfterWriteListener"))
                                .addListener(Jsl.listener("failAfterWriteListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-after-write", PARAMETERS);
        try {
            operation.get();
            fail();
        } catch (final ExecutionException e){
            //
        }
        Assert.assertArrayEquals(new OrderEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE /* throws */, ON_WRITE_ERROR,
                ON_CHUNK_ERROR,
                ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }
}
