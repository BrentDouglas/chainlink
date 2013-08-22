package io.machinecode.nock.spi;

import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Step;

import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Repository {

    // Create

    JobInstance createJobInstance(final Job job);

    JobExecution createJobExecution(final JobInstance jobInstance);

    StepExecution createStepExecution(final JobExecution jobExecution, final Step<?,?> step);

    // JobOperator

    Job getJob(final String name) throws NoSuchJobException, JobSecurityException;

    Set<String> getJobNames() throws JobSecurityException;

    int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException;

    List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException;

    List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException;

    Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException;

    JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException;

    List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException;
}
