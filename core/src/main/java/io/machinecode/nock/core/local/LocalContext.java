package io.machinecode.nock.core.local;

import io.machinecode.nock.core.impl.JobContextImpl;
import io.machinecode.nock.core.inject.InjectionContextImpl;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.Worker;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.StepExecution;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.Properties;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalContext implements Context {

    final RuntimeConfiguration configuration;
    final Repository repository;
    final InjectionContext injectionContext;
    final Properties properties;
    final Worker worker;
    final long instanceId;
    final long executionId;
    final JobWork job;
    JobContext jobContext;
    final ThreadLocal<StepContext> stepContext = new ThreadLocal<StepContext>();

    LocalContext(final JobInstance instance, final JobExecution execution, final JobWork job, final Properties properties,
                 final RuntimeConfiguration configuration, final Repository repository, final Worker worker) {
        this.injectionContext = new InjectionContextImpl(
                configuration.getClassLoader(),
                configuration.getArtifactLoader()
        );
        this.properties = properties;
        this.worker = worker;
        this.instanceId = instance.getInstanceId();
        this.executionId = execution.getExecutionId();
        this.configuration = configuration;
        this.repository = repository;
        this.job = job;
        this.jobContext = new JobContextImpl(instance, execution, properties);
    }

    @Override
    public JobWork getJob() {
        return this.job;
    }

    @Override
    public long getJobInstanceId() {
        return this.instanceId;
    }

    @Override
    public long getJobExecutionId() {
        return executionId;
    }

    @Override
    public long[] getStepExecutionIds() {
        return new long[0]; //TODO ?
    }

    @Override
    public JobContext getJobContext() {
        return this.jobContext;
    }

    @Override
    public void setJobContext(JobContext jobContext) {
        this.jobContext = jobContext;
    }

    @Override
    public StepContext getStepContext() {
        return this.stepContext.get();
    }

    @Override
    public void setStepContext(final StepContext stepContext) {
        this.stepContext.set(stepContext);
    }
}
