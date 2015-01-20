package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.core.DeferredImpl;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedProxyExecutionRepository<A> implements ExecutionRepository {

    protected final Transport<A> transport;
    protected final A address;
    protected final ExecutionRepositoryId executionRepositoryId;
    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedProxyExecutionRepository(final Transport<A> transport, final ExecutionRepositoryId executionRepositoryId, final A address) {
        this.transport = transport;
        this.address = address;
        this.executionRepositoryId = executionRepositoryId;
        this.timeout = transport.getTimeout();
        this.unit = transport.getTimeUnit();
    }

    protected abstract <T> Command<T, A> _cmd(final String name, final Serializable... params);

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<>();
            transport.invokeRemote(address, this.<ExtendedJobInstance>_cmd("createJobInstance", jobId, jslName, timestamp), promise, this.timeout, this.unit);
            return promise.get(timeout, unit);
    }

    @Override
    public ExtendedJobExecution createJobExecution(final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws Exception {
        final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedJobExecution>_cmd("createJobExecution", jobInstanceId, jobName, parameters, timestamp), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception {
        final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("createStepExecution", jobExecutionId, stepName, timestamp), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final DeferredImpl<PartitionExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<PartitionExecution>_cmd("createPartitionExecution", stepExecutionId, partitionId, properties, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("startJobExecution", jobExecutionId, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("updateJobExecution", jobExecutionId, batchStatus, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("finishJobExecution", jobExecutionId, batchStatus, exitStatus, restartElementId, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("linkJobExecutions", jobExecutionId, restartJobExecutionId), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("startStepExecution", stepExecutionId, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("finishStepExecution", stepExecutionId, metrics, batchStatus, exitStatus, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("startPartitionExecution", partitionExecutionId, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("updatePartitionExecution", partitionExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Void>_cmd("finishPartitionExecution", partitionExecutionId, metrics, persistentUserData, batchStatus, exitStatus, timestamp), promise, this.timeout, this.unit);
        promise.get(timeout, unit);
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        final DeferredImpl<Set<String>,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Set<String>>_cmd("getJobNames"), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        final DeferredImpl<Integer,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Integer>_cmd("getJobInstanceCount", jobName), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        final DeferredImpl<List<JobInstance>,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<List<JobInstance>>_cmd("getJobInstances", jobName, start, count), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        final DeferredImpl<List<Long>,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<List<Long>>_cmd("getRunningExecutions", jobName), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        final DeferredImpl<Properties,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Properties>_cmd("getParameters", jobExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedJobInstance>_cmd("getJobInstance", jobInstanceId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedJobInstance>_cmd("getJobInstanceForExecution", jobExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        final DeferredImpl<List<? extends JobExecution>,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<List<? extends JobExecution>>_cmd("getJobExecutions", jobInstanceId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedJobExecution>_cmd("getJobExecution", jobExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedJobExecution>_cmd("restartJobExecution", jobExecutionId, parameters), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        final DeferredImpl<List<? extends StepExecution>,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<List<? extends StepExecution>>_cmd("getStepExecutionsForJobExecution", jobExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("getStepExecution", stepExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("getPreviousStepExecution", jobExecutionId, stepExecutionId, stepName), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("getLatestStepExecution", jobExecutionId, stepName), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        final DeferredImpl<Integer,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<Integer>_cmd("getStepExecutionCount", jobExecutionId, stepName), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        final DeferredImpl<StepExecution[],Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<StepExecution[]>_cmd("getStepExecutions", new Serializable[]{stepExecutionIds}), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        final DeferredImpl<PartitionExecution[],Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<PartitionExecution[]>_cmd("getUnfinishedPartitionExecutions", stepExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        final DeferredImpl<PartitionExecution,Throwable,Void> promise = new DeferredImpl<>();
        transport.invokeRemote(address, this.<PartitionExecution>_cmd("getPartitionExecution", partitionExecutionId), promise, this.timeout, this.unit);
        return promise.get(timeout, unit);
    }
}
