package io.machinecode.nock.test.core.transport;

import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.configuration.RuntimeConfigurationImpl;
import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.core.local.LocalTransport;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.core.work.ContextImpl;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.test.core.transport.artifact.batchlet.FailBatchlet;
import io.machinecode.nock.test.core.transport.artifact.batchlet.RunBatchlet;
import io.machinecode.nock.test.core.transport.artifact.batchlet.StopBatchlet;
import org.junit.Assert;
import org.junit.Test;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import java.util.Properties;
import java.util.concurrent.CancellationException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class LocalTransportTest extends Assert {

    public static final Properties PARAMETERS = new Properties();
    private Transport _transport;
    private Repository _repository;
    private RuntimeConfigurationImpl _configuration;

    protected final RuntimeConfigurationImpl configuration() {
        if (this._configuration == null) {
            this._configuration = new RuntimeConfigurationImpl(_configuration().build());
        }
        return this._configuration;
    }

    protected final Repository repository() {
        if (this._repository == null) {
            this._repository = _repository();
        }
        return _repository;
    }

    protected final Transport transport() {
        if (this._transport == null) {
            this._transport = _transport();
        }
        return _transport;
    }

    // Override these for different configurations

    protected Builder _configuration() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setTransactionManager(new LocalTransactionManager(180))
                .setRepository(repository())
                .setJobLoaders(new JobLoader[0])
                .setArtifactLoaders(new ArtifactLoader[0]);
    }

    protected final Repository _repository() {
        return new LocalRepository();
    }

    protected final Transport _transport() {
        return new LocalTransport(configuration(), 1);
    }

    @Test
    public void runBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("run-batchlet")
                                )
                ), PARAMETERS);
        final JobInstance instance = repository().createJobInstance(job);
        final JobExecution execution = repository().createJobExecution(instance);
        Assert.assertEquals(BatchStatus.STARTING, execution.getBatchStatus());
        Assert.assertEquals(BatchStatus.STARTING.name(), execution.getExitStatus());
        final ContextImpl context = new ContextImpl(
                job,
                instance.getInstanceId(),
                execution.getExecutionId()
        );
        final Deferred<?> deferred = transport().executeJob(context.getJobExecutionId(), job, context);
        deferred.get();
        Assert.assertTrue(RunBatchlet.hasRun.get());
        Assert.assertEquals(BatchStatus.COMPLETED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals(BatchStatus.COMPLETED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void stopBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("stop-batchlet")
                                )
                ), PARAMETERS);
        final JobInstance instance = repository().createJobInstance(job);
        final JobExecution execution = repository().createJobExecution(instance);
        Assert.assertEquals(BatchStatus.STARTING, execution.getBatchStatus());
        Assert.assertEquals(BatchStatus.STARTING.name(), execution.getExitStatus());
        final ContextImpl context = new ContextImpl(
                job,
                instance.getInstanceId(),
                execution.getExecutionId()
        );
        final Deferred<?> deferred = transport().executeJob(context.getJobExecutionId(), job, context);
        Thread.sleep(100);
        Assert.assertTrue(StopBatchlet.hasRun.get());
        Assert.assertTrue(deferred.cancel(true));
        try {
            deferred.get();
            Assert.fail();
        } catch (final CancellationException e) {
            //
        }
        Assert.assertTrue(StopBatchlet.hasStopped.get());
        Assert.assertEquals(BatchStatus.STOPPED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals(BatchStatus.STOPPED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }

    @Test
    public void failBatchletTest() throws Exception {
        final JobImpl job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("job")
                .addExecution(
                        Jsl.stepWithBatchletAndPlan()
                                .setId("step")
                                .setTask(
                                        Jsl.batchlet()
                                                .setRef("fail-batchlet")
                                )
                ), PARAMETERS);
        final JobInstance instance = repository().createJobInstance(job);
        final JobExecution execution = repository().createJobExecution(instance);
        Assert.assertEquals(BatchStatus.STARTING, execution.getBatchStatus());
        Assert.assertEquals(BatchStatus.STARTING.name(), execution.getExitStatus());
        final ContextImpl context = new ContextImpl(
                job,
                instance.getInstanceId(),
                execution.getExecutionId()
        );
        final Deferred<?> deferred = transport().executeJob(context.getJobExecutionId(), job, context);
        deferred.get();
        Assert.assertTrue(FailBatchlet.hasRun.get());
        Assert.assertEquals(BatchStatus.FAILED, repository().getJobExecution(execution.getExecutionId()).getBatchStatus());
        Assert.assertEquals(BatchStatus.FAILED.name(), repository().getJobExecution(execution.getExecutionId()).getExitStatus());
    }
}
