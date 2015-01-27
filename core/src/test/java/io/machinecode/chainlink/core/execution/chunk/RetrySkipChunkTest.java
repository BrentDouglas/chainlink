package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent;
import io.machinecode.chainlink.core.execution.chunk.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailReadException;
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
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ON_READ_ERROR;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.PROCESS;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READ;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READER_CLOSE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.READER_OPEN;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.RETRY_READ_EXCEPTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.SKIP_READ;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.chunk.artifact.ChunkEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RetrySkipChunkTest extends EventOrderTest {

    @Test
    public void retrySkipSupertypeChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setReader(Jsl.reader("failTwiceTwiceReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-skip-supertype", PARAMETERS);
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
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
    public void retrySkipReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setReader(Jsl.reader("failTwiceTwiceReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-skip-limit-read-item", PARAMETERS);
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
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
    public void retrySkipSkipLimitReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setSkipLimit("1")
                                                .setReader(Jsl.reader("failTwiceTwiceReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-skip-skip-limit-read-item", PARAMETERS);
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
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION, //Skip limit is exhausted so matches retry
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

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
    public void retrySkipRetryLimitReadChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setRetryLimit("1")
                                                .setReader(Jsl.reader("failTwiceTwiceReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-skip-limit-read-item", PARAMETERS);
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
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ, //Retry limit exhausted so matches skip
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
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
}
