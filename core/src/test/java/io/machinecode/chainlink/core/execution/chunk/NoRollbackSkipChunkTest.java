package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailProcessException;
import io.machinecode.chainlink.core.execution.artifact.exception.FailReadException;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteException;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.spi.jsl.Job;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

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
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.RETRY_PROCESS_EXCEPTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.RETRY_READ_EXCEPTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.RETRY_WRITE_EXCEPTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.SKIP_PROCESS;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.SKIP_READ;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.SKIP_WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoRollbackSkipChunkTest extends EventOrderTest {

    @Test
    public void noRollbackReadChunkTest() throws Exception {
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
                                                .setReader(Jsl.reader("failTripleTwiceReadEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-skip-read", PARAMETERS);
        operation.get();
        final OrderEvent[] events = EventOrderAccumulator.order();
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
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, SKIP_READ,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
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
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION,
                BEFORE_READ, READ, AFTER_READ,
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB
        }, events);
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void noRollbackProcessChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("4")
                                                .setSkipLimit("1")
                                                .setReader(Jsl.reader("sixEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("failTripleTwiceEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-skip-process", PARAMETERS);
        operation.get();
        final OrderEvent[] events = EventOrderAccumulator.order();
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
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, RETRY_PROCESS_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

                BEGIN_TRANSACTION,
                BEFORE_CHUNK,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, SKIP_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, RETRY_PROCESS_EXCEPTION,
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
        }, events);
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }

    @Test
    public void noRollbackWriteChunkTest() throws Exception {
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
                                                .setWriter(Jsl.writer("failTripleTwiceEventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                                .setNoRollbackExceptionClasses(Jsl.noRollbackExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                                .setSkippableExceptionClasses(Jsl.skippableExceptionClasses()
                                                        .addInclude(Exception.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-rollback-skip-write", PARAMETERS);
        operation.get();
        final OrderEvent[] events = EventOrderAccumulator.order();
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
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, RETRY_WRITE_EXCEPTION,
                WRITER_CLOSE, READER_CLOSE,
                ON_CHUNK_ERROR, ROLLBACK_TRANSACTION,
                BEGIN_TRANSACTION, READER_OPEN, WRITER_OPEN, COMMIT_TRANSACTION,

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
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, RETRY_WRITE_EXCEPTION,
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
        }, events);
        assertJobFinishedWith(operation, BatchStatus.COMPLETED);
    }
}
