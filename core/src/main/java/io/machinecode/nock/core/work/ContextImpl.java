package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.work.JobWork;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ContextImpl implements Context {

    private final JobWork job;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private final long[] stepExecutionIds;
    private JobContext jobContext;
    private StepContext stepContext;

    public ContextImpl(final JobWork job, final long jobInstanceId, final long jobExecutionId, final long[] stepExecutionIds) {
        this.job = job;
        this.jobInstanceId = jobInstanceId;
        this.jobExecutionId = jobExecutionId;
        this.stepExecutionIds = stepExecutionIds;
    }

    @Override
    public JobWork getJob() {
        return job;
    }

    @Override
    public long getJobInstanceId() {
        return jobInstanceId;
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public long[] getStepExecutionIds() {
        return stepExecutionIds;
    }

    @Override
    public JobContext getJobContext() {
        return jobContext;
    }

    @Override
    public void setJobContext(final JobContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public StepContext getStepContext() {
        return stepContext;
    }

    @Override
    public void setStepContext(final StepContext stepContext) {
        this.stepContext = stepContext;
    }
}
