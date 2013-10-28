package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.work.JobWork;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ContextImpl implements Context {

    private final JobWork job;
    private final RestartableJobExecution jobExecution;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private MutableJobContext parentJobContext;
    private final ThreadLocal<MutableJobContext> jobContexts = new ThreadLocal<MutableJobContext>();
    private final ThreadLocal<long[]> stepExecutionIds = new ThreadLocal<long[]>(); //TODO Probably can't be a threadlocal. Think about this
    private final ThreadLocal<MutableStepContext> stepContext = new ThreadLocal<MutableStepContext>();
    private Throwable throwable;

    public ContextImpl(final JobWork job, final long jobInstanceId, final RestartableJobExecution jobExecution) {
        this.job = job;
        this.jobInstanceId = jobInstanceId;
        this.jobExecution = jobExecution;
        this.jobExecutionId = jobExecution.getExecutionId();
    }

    @Override
    public JobWork getJob() {
        return job;
    }

    @Override
    public RestartableJobExecution getJobExecution() {
        return jobExecution;
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
        return stepExecutionIds.get();
    }

    @Override
    public void setStepExecutionIds(final long[] stepExecutionIds) {
        this.stepExecutionIds.set(stepExecutionIds);
    }

    @Override
    public MutableJobContext getJobContext() {
        if (jobContexts.get() == null && parentJobContext != null) {
            jobContexts.set(parentJobContext.copy());
        }
        return jobContexts.get();
    }

    @Override
    public void setJobContext(final MutableJobContext jobContext) {
        this.jobContexts.set(jobContext);
    }

    @Override
    public void setParentJobContext(final MutableJobContext jobContext) {
        this.parentJobContext = jobContext;
        this.jobContexts.set(jobContext);
    }

    @Override
    public MutableStepContext getStepContext() {
        return stepContext.get();
    }

    @Override
    public void setStepContext(final MutableStepContext stepContext) {
        this.stepContext.set(stepContext);
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void setThrowable(final Throwable throwable) {
        this.throwable = throwable;
    }
}
