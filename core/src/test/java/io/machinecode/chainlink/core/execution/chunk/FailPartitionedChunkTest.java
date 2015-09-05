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

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.management.JobOperationImpl;
import io.machinecode.chainlink.spi.jsl.Job;
import org.junit.Ignore;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Ignore //TODO See why this is locking up
public class FailPartitionedChunkTest extends EventOrderTest {

    @Test
    public void failCollectThrowsChunkTest() throws Exception {
        printMethodName();
        EventOrderAccumulator.reset();
        final Job job = Jsl.job("job")
                .addListener(Jsl.listener("eventOrderListener"))
                .addExecution(
                        Jsl.step("step")
                                .setPartition(Jsl.partition().setStrategy(Jsl.plan()
                                        .setPartitions(2)
                                        .setThreads(2))
                                        .setCollector(Jsl.collector("failEventOrderCollector"))
                                )
                                .setTask(
                                        Jsl.chunk()
                                                .setReader(Jsl.reader("stopEventOrderReader"))
                                                .setWriter(Jsl.writer("eventOrderWriter"))
                                                .setProcessor(Jsl.processor("neverEventOrderProcessor"))
                                )
                );
        final JobOperationImpl operation = operator.startJob(job, "fail-collect-one-item", PARAMETERS);
        operation.get();
        assertJobFinishedWith(operation, BatchStatus.FAILED);
    }
}
