package io.machinecode.nock.spi;

import io.machinecode.nock.spi.element.Job;

import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Repository {

    JobDescriptor add(final Job job, final String id);

    // Create

    JobInstance createJobInstance(final JobDescriptor job);

    JobExecution createJobExecution(final JobInstance jobInstance);

    StepExecution createStepExecution(final StepDescriptor step);

    // Find

    JobDescriptor findJob(final long id) throws NoSuchJobException;

    JobInstance findJobInstance(final long id) throws NoSuchJobInstanceException;

    JobExecution findJobExecution(final long id) throws NoSuchJobExecutionException;

    StepDescriptor findStep(final long id);

    StepExecution findStepExecution(final long id);
}
