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
package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.then.api.Promise;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.runtime.JobExecution;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobOperationImpl implements JobOperation {

    private static final Logger log = Logger.getLogger(JobOperationImpl.class);

    private final long jobExecutionId;
    private final Promise<?,?,?> promise;
    private final Repository repository;

    public JobOperationImpl(final long jobExecutionId, final Promise<?,?,?> promise, final Repository repository) {
        this.jobExecutionId = jobExecutionId;
        this.promise = promise;
        this.repository = repository;
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return promise != null && promise.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return promise != null && promise.isCancelled();
    }

    @Override
    public boolean isDone() {
        return promise == null || promise.isDone();
    }

    @Override
    public JobExecution get() throws InterruptedException, ExecutionException {
        if (promise != null) {
            promise.get();
        }
        try {
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            log.tracef(Messages.get("CHAINLINK-033000.operation.get"), jobExecutionId, execution);
            return execution;
        } catch (final BatchRuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public JobExecution get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (promise != null) {
            promise.get(timeout, unit);
        }
        try {
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            log.tracef(Messages.get("CHAINLINK-033000.operation.get"), jobExecutionId, execution);
            return execution;
        } catch (final BatchRuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     * @return The {@link JobExecution} once the job has terminated.
     * @throws BatchRuntimeException On an error fetching the {@link JobExecution} for the job.
     */
    public JobExecution safeGet() throws InterruptedException {
        if (promise != null) {
            try {
                promise.get();
            } catch (final ExecutionException | CancellationException e) {
                //
            }
        }
        try {
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            log.tracef(Messages.get("CHAINLINK-033000.operation.get"), jobExecutionId, execution);
            return execution;
        } catch (final BatchRuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    /**
     * Similar to {@link #get(long, java.util.concurrent.TimeUnit)} but guaranteed not to throw a
     * {@link java.util.concurrent.CancellationException} or a {@link java.util.concurrent.ExecutionException}.
     * @return The {@link JobExecution} once the job has terminated.
     * @throws BatchRuntimeException On an error fetching the {@link JobExecution} for the job.
     */
    public JobExecution safeGet(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException {
        if (promise != null) {
            try {
                promise.get(timeout, unit);
            } catch (final ExecutionException | CancellationException e) {
                //
            }
        }
        try {
            final ExtendedJobExecution execution = repository.getJobExecution(jobExecutionId);
            log.tracef(Messages.get("CHAINLINK-033000.operation.get"), jobExecutionId, execution);
            return execution;
        } catch (final BatchRuntimeException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }
}
