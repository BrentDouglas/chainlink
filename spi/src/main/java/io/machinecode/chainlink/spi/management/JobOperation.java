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
package io.machinecode.chainlink.spi.management;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobOperation extends Future<JobExecution> {

    /**
     * @return The id of the {@link JobExecution} that will be returned. Will match the result of
     *         calling {@link javax.batch.runtime.JobExecution#getExecutionId()} on the result of
     *         {@link #get()} or it's variants.
     */
    long getJobExecutionId();

    /**
     * {@inheritDoc}
     * @throws BatchRuntimeException On an error fetching the {@link JobExecution} for the job.
     */
    JobExecution get() throws BatchRuntimeException, InterruptedException, ExecutionException, CancellationException;

    /**
     * {@inheritDoc}
     * @throws BatchRuntimeException On an error fetching the {@link JobExecution} for the job.
     */
    JobExecution get(long timeout, TimeUnit unit) throws BatchRuntimeException, InterruptedException, ExecutionException, CancellationException, TimeoutException;
}
