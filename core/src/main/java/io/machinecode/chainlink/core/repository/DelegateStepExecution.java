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
package io.machinecode.chainlink.core.repository;

import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.Repository;

import javax.batch.operations.BatchRuntimeException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @see https://java.net/bugzilla/show_bug.cgi?id=4834
 */
public class DelegateStepExecution implements ExtendedStepExecution {
    private final long executionId;
    private final String stepName;
    private final Repository repository;

    public DelegateStepExecution(final StepExecution execution, final Repository repository) {
        this.executionId = execution.getStepExecutionId();
        this.stepName = execution.getStepName();
        this.repository = repository;
    }

    @Override
    public long getStepExecutionId() {
        return executionId;
    }

    @Override
    public String getStepName() {
        return stepName;
    }

    @Override
    public BatchStatus getBatchStatus() {
        try {
            return repository.getStepExecution(executionId).getBatchStatus();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getCreateTime() {
        try {
            return repository.getStepExecution(executionId).getCreateTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getStartTime() {
        try {
            return repository.getStepExecution(executionId).getStartTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getUpdatedTime() {
        try {
            return repository.getStepExecution(executionId).getUpdatedTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Date getEndTime() {
        try {
            return repository.getStepExecution(executionId).getEndTime();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public String getExitStatus() {
        try {
            return repository.getStepExecution(executionId).getExitStatus();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Serializable getPersistentUserData() {
        try {
            return repository.getStepExecution(executionId).getPersistentUserData();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Metric[] getMetrics() {
        try {
            return repository.getStepExecution(executionId).getMetrics();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Serializable getReaderCheckpoint() {

        try {
            return repository.getStepExecution(executionId).getReaderCheckpoint();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public Serializable getWriterCheckpoint() {

        try {
            return repository.getStepExecution(executionId).getWriterCheckpoint();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public long getJobExecutionId() {
        try {
            return repository.getStepExecution(executionId).getJobExecutionId();
        } catch (final NoSuchJobExecutionException | JobSecurityException e) {
            throw e;
        } catch (final Exception e) {
            throw new BatchRuntimeException(e);
        }
    }
}
