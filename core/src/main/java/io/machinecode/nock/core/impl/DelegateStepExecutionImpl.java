package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.ExecutionRepository;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;

/**
 * @see https://java.net/bugzilla/show_bug.cgi?id=4834
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class DelegateStepExecutionImpl implements StepExecution {
    private final long executionId;
    private final String stepName;
    private final ExecutionRepository repository;

    public DelegateStepExecutionImpl(final StepExecution execution, final ExecutionRepository repository) {
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
        return repository.getStepExecution(executionId).getBatchStatus();
    }

    @Override
    public Date getStartTime() {
        return repository.getStepExecution(executionId).getStartTime();
    }

    @Override
    public Date getEndTime() {
        return repository.getStepExecution(executionId).getEndTime();
    }

    @Override
    public String getExitStatus() {
        return repository.getStepExecution(executionId).getExitStatus();
    }

    @Override
    public Serializable getPersistentUserData() {
        return repository.getStepExecution(executionId).getPersistentUserData();
    }

    @Override
    public Metric[] getMetrics() {
        return repository.getStepExecution(executionId).getMetrics();
    }
}
