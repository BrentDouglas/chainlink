package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.work.JobWork;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ContextImpl implements Context {

    private final JobWork job;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private final long[] stepExecutionIds;
    private MutableJobContext jobContext;
    private final ThreadLocal<MutableStepContext> stepContext = new ThreadLocal<MutableStepContext>();

    public ContextImpl(final JobWork job, final long jobInstanceId, final long jobExecutionId, final long... stepExecutionIds) {
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
        return stepExecutionIds; //TODO This is wrong
    }

    @Override
    public MutableJobContext getJobContext() {
        return jobContext;
    }

    @Override
    public void setJobContext(final MutableJobContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public MutableStepContext getStepContext() {
        return stepContext.get();
    }

    @Override
    public void setStepContext(final MutableStepContext stepContext) {
        this.stepContext.set(stepContext);
    }
}
