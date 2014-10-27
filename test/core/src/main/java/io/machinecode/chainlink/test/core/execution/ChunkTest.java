package io.machinecode.chainlink.test.core.execution;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderAccumulator;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent;
import io.machinecode.chainlink.test.core.execution.artifact.chunk.EventOrderTransactionManager;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.transaction.TransactionManager;

import java.util.concurrent.TimeUnit;

import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_JOB;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_STEP;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEGIN_TRANSACTION;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.COMMIT_TRANSACTION;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READER_OPEN;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITER_OPEN;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_CHUNK;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_READ;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READ;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_READ;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_PROCESS;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.PROCESS;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_PROCESS;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.BEFORE_WRITE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_WRITE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_CHUNK;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READER_CHECKPOINT;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.READER_CLOSE;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_STEP;
import static io.machinecode.chainlink.test.core.execution.artifact.chunk.ChunkEvent.AFTER_JOB;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public abstract class ChunkTest extends OperatorTest {


    protected TransactionManager _transactionManager() throws Exception {
        return new EventOrderTransactionManager(180, TimeUnit.SECONDS);
    }


    @Test
    public void noItemChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("neverEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "no-item", PARAMETERS);
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
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,
                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void oneItemChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("oneEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "one-item", PARAMETERS);
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
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void oneItemOneCountChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("1")
                                                .setReader(Jsl.reader().setRef("oneEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "one-item-one-count", PARAMETERS);
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
                AFTER_CHUNK,
                READER_CHECKPOINT, WRITER_CHECKPOINT,
                COMMIT_TRANSACTION,
                BEGIN_TRANSACTION,
                WRITER_CLOSE, READER_CLOSE,
                COMMIT_TRANSACTION,
                AFTER_STEP,
                AFTER_JOB

        }, EventOrderAccumulator.order());
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void sixItemsChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "six-items", PARAMETERS);
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
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void sixItemsTwoCountChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("2")
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "six-items-two-count", PARAMETERS);
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
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void sixItemsFourCountChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("4")
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "six-items-four-count", PARAMETERS);
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
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }

    @Test
    public void sixItemsSixCountChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job()
                .setId("job")
                .addListener(Jsl.listener().setRef("eventOrderListener"))
                .addExecution(
                        Jsl.step()
                                .setId("step")
                                .setTask(
                                        Jsl.chunk()
                                                .setItemCount("6")
                                                .setReader(Jsl.reader().setRef("sixEventOrderReader"))
                                                .setWriter(Jsl.writer().setRef("eventOrderWriter"))
                                                .setProcessor(Jsl.processor().setRef("neverEventOrderProcessor"))
                                ).addListener(Jsl.listener().setRef("eventOrderListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "six-items-six-count", PARAMETERS);
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
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
                BEFORE_READ, READ, AFTER_READ,
                BEFORE_PROCESS, PROCESS, AFTER_PROCESS,
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
        _assertFinishedWith(BatchStatus.COMPLETED, operation.getJobExecutionId());
    }
}
