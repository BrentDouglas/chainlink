package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
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
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.RETRY_PROCESS_EXCEPTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.RETRY_READ_EXCEPTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.RETRY_WRITE_EXCEPTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoRollbackChunkTest extends EventOrderTest {

    @Test
    public void noRollbackSupertypeChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setRetryLimit("1")
                                                .setReader(Jsl.reader("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-supertype", PARAMETERS);
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
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackLimitReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setRetryLimit("2")
                                                .setReader(Jsl.reader("failReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-limit-read-item", PARAMETERS);
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
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackOnceReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setRetryLimit("2")
                                                .setReader(Jsl.reader("onceFailReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-once-read-item", PARAMETERS);
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
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
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
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackOnceReadCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader("onceFailCheckpointEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadCheckpointException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailReadCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-once-read-checkpoint", PARAMETERS);
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
                READER_CHECKPOINT /* throws */, // TODO RETRY_READ_EXCEPTION,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
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
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackLimitReadCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setRetryLimit("1")
                                                .setReader(Jsl.reader("failCheckpointEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadCheckpointException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailReadCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-limit-read-checkpoint", PARAMETERS);
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
                READER_CHECKPOINT /* throws */, // TODO RETRY_READ_EXCEPTION,
                READER_CHECKPOINT /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackLimitProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setRetryLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("failEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-limit-process-item", PARAMETERS);
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
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, RETRY_PROCESS_EXCEPTION,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, RETRY_PROCESS_EXCEPTION,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackOnceProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setRetryLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("onceFailEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-once-process-item", PARAMETERS);
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
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, RETRY_PROCESS_EXCEPTION,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
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
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackLimitWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setRetryLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("failWriteEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-limit-write-item", PARAMETERS);
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
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, RETRY_WRITE_EXCEPTION,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, RETRY_WRITE_EXCEPTION,
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackOnceWriteChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setRetryLimit("2")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("onceFailWriteEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-once-write-item", PARAMETERS);
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
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, RETRY_WRITE_EXCEPTION,
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
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackOnceWriteCheckpointTest() throws Exception {
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
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailWriteCheckpointException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailWriteCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-once-write-checkpoint", PARAMETERS);
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
                READER_CHECKPOINT, WRITER_CHECKPOINT /* throws */, // TODO RETRY_WRITE_EXCEPTION,
                WRITER_CHECKPOINT,
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
        assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void noRollbackLimitWriteCheckpointTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setRetryLimit("1")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("failCheckpointEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailWriteCheckpointException.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailWriteCheckpointException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-limit-write-checkpoint", PARAMETERS);
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
                READER_CHECKPOINT, WRITER_CHECKPOINT /* throws */, // TODO RETRY_WRITE_EXCEPTION,
                WRITER_CHECKPOINT /* throws */,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, EventOrderAccumulator.order());
        assertFinishedWith(BatchStatus.FAILED, operation.getJobExecutionId());
    }

}