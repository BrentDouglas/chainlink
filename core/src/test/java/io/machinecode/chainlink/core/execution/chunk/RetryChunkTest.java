/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.execution.chunk;

import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.exception.FailProcessException;
import io.machinecode.chainlink.core.execution.artifact.exception.FailReadException;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteException;
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
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.RETRY_PROCESS_EXCEPTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.RETRY_READ_EXCEPTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.RETRY_WRITE_EXCEPTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.ROLLBACK_TRANSACTION;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CHECKPOINT;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_CLOSE;
import static io.machinecode.chainlink.core.execution.artifact.OrderEvent.WRITER_OPEN;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RetryChunkTest extends EventOrderTest {

    @Test
    public void retryReadChunkTest() throws Exception {
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
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailReadException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failRetryReadListener"))
                                .addListener(Jsl.listener("failRetryReadListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-read-item", PARAMETERS);
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
                BEFORE_READ, READ /* throws */, ON_READ_ERROR, RETRY_READ_EXCEPTION /* throws */,
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
    public void retryProcessChunkTest() throws Exception {
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
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailProcessException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failRetryProcessListener"))
                                .addListener(Jsl.listener("failRetryProcessListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-process-item", PARAMETERS);
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
                BEFORE_PROCESS, PROCESS /* throws */, ON_PROCESS_ERROR, RETRY_PROCESS_EXCEPTION /* throws */,
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
    public void retryWriteChunkTest() throws Exception {
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
                                                .setRetryableExceptionClasses(Jsl.retryableExceptionClasses()
                                                        .addInclude(FailWriteException.class)
                                                )
                                ).addListener(Jsl.listener("eventOrderListener"))
                                .addListener(Jsl.listener("failRetryWriteListener"))
                                .addListener(Jsl.listener("failRetryWriteListener"))
                );
        final JobOperationImpl operation = operator.startJob(job, "retry-write-item", PARAMETERS);
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
                BEFORE_WRITE, WRITE /* throws */, ON_WRITE_ERROR, RETRY_WRITE_EXCEPTION /* throws */,
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
