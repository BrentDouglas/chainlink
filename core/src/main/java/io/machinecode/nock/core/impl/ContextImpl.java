package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.context.MutableJobContext;
import io.machinecode.nock.spi.context.MutableStepContext;
import io.machinecode.nock.spi.util.Pair;
import io.machinecode.nock.spi.work.JobWork;

import java.util.List;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ContextImpl implements Context {

    private final JobWork job;
    private final long jobInstanceId;
    private final long jobExecutionId;
    private final ThreadLocal<long[]> stepExecutionIds = new ThreadLocal<long[]>(); //TODO Probably can't be a threadlocal. Think about this
    private MutableJobContext jobContext;
    private final ThreadLocal<MutableStepContext> stepContext = new ThreadLocal<MutableStepContext>();
    private Throwable throwable;

    public ContextImpl(final JobWork job, final long jobInstanceId, final long jobExecutionId) {
        this.job = job;
        this.jobInstanceId = jobInstanceId;
        this.jobExecutionId = jobExecutionId;
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
        return stepExecutionIds.get();
    }

    @Override
    public void setStepExecutionIds(final long[] stepExecutionIds) {
        this.stepExecutionIds.set(stepExecutionIds);
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

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    @Override
    public void setThrowable(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public List<? extends Pair<String, String>> getProperties() {
        return null;//properties;
    }
}
