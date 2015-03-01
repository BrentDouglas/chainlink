package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent;
import io.machinecode.chainlink.core.execution.chunk.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailProcessException;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailReadCheckpointException;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailReadException;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailWriteCheckpointException;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailWriteException;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.AFTER_CHUNK;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.AFTER_JOB;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.AFTER_PROCESS;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.AFTER_READ;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.AFTER_STEP;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.AFTER_WRITE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEFORE_CHUNK;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEFORE_JOB;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEFORE_PROCESS;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEFORE_READ;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEFORE_STEP;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEFORE_WRITE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.BEGIN_TRANSACTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.COMMIT_TRANSACTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ON_CHUNK_ERROR;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ON_PROCESS_ERROR;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ON_READ_ERROR;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ON_WRITE_ERROR;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.PROCESS;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READ;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READER_CLOSE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READER_OPEN;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.SKIP_PROCESS;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.SKIP_READ;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.SKIP_WRITE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SkipChunkTest extends EventOrderTest {

    @Test
    public void skipSupertypeChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setSkipLimit("1")
                                                .setReader(Jsl.reader("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-supertype", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void skipLimitReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setSkipLimit("2")
                                                .setReader(Jsl.reader("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-limit-read-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void skipOnceReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setSkipLimit("2")
                                                .setReader(Jsl.reader("onceFailReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-once-read-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
                BEFORE_JOB,
                BEFORE_STEP,
                BEGIN_TRANSACTION,
                READER_OPEN, WRITER_OPEN,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void skipOnceReadCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setReader(Jsl.reader("onceFailCheckpointEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-once-read-checkpoint", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT /* throws */, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void skipLimitReadCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setSkipLimit("1")
                                                .setReader(Jsl.reader("failCheckpointEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-limit-read-checkpoint", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                READER_CHECKPOINT /* throws */, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void skipLimitProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setSkipLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("failEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-limit-process-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, SKIP_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, SKIP_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void skipOnceProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setSkipLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("onceFailEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-once-process-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, SKIP_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void skipLimitWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setSkipLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("failWriteEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-limit-write-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, SKIP_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, SKIP_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

    @Test
    public void skipOnceWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setSkipLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("onceFailWriteEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-once-write-item", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, SKIP_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void skipOnceWriteCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("onceFailCheckpointEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailWriteCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-once-write-checkpoint", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT /* throws */,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
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
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void skipLimitWriteCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setSkipLimit("1")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("failCheckpointEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailWriteCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "skip-limit-write-checkpoint", PARAMETERS);
        operation.get();
        Assert.assertArrayEquals(new ChunkEvent[]{
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
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT /* throws */,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_WRITE, WRITE, AFTER_WRITE,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }

}
