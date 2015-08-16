/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * <p>The outcome of the execution of a single {@link io.machinecode.chainlink.spi.jsl.task.Task}
 *  (a batchlet or chunk invocation on a single thread).</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Item {

    /**
     * @return The result of {@link javax.batch.api.partition.PartitionCollector#collectPartitionData()}
     *         or {@code null} if no collector was specified.
     */
    Serializable getData();

    /**
     * @return The result of {@link javax.batch.runtime.context.StepContext#getBatchStatus()} after
     *         the task completed.
     */
    BatchStatus getBatchStatus();

    /**
     * @return The result of {@link javax.batch.runtime.context.StepContext#getExitStatus()} after
     *         the task completed.
     */
    String getExitStatus();
}
