package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.core.transport.cmd.InvokeRepositoryCommand;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.repository.Repository;

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
public class DistributedProxyRepository implements Repository {

    protected final DistributedTransport<?> transport;
    protected final Object address;
    protected final RepositoryId repositoryId;
    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedProxyRepository(final DistributedTransport<?> transport, final RepositoryId repositoryId) {
        this.transport = transport;
        this.address = repositoryId.getAddress();
        this.repositoryId = repositoryId;
        this.timeout = transport.getTimeout();
        this.unit = transport.getTimeUnit();
    }

    protected <T> Command<T> _cmd(final String name, final Serializable... params) {
        return new InvokeRepositoryCommand<>(repositoryId, name, params);
    }

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedJobInstance>_cmd("createJobInstance", jobId, jslName, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedJobExecution createJobExecution(final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedJobExecution>_cmd("createJobExecution", jobInstanceId, jobName, parameters, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("createStepExecution", jobExecutionId, stepName, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        return transport.invokeRemote(address, this.<PartitionExecution>_cmd("createPartitionExecution", stepExecutionId, partitionId, properties, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("startJobExecution", jobExecutionId, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("updateJobExecution", jobExecutionId, batchStatus, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("finishJobExecution", jobExecutionId, batchStatus, exitStatus, restartElementId, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("linkJobExecutions", jobExecutionId, restartJobExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("startStepExecution", stepExecutionId, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("finishStepExecution", stepExecutionId, metrics, batchStatus, exitStatus, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("startPartitionExecution", partitionExecutionId, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("updatePartitionExecution", partitionExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        transport.invokeRemote(address, this.<Void>_cmd("finishPartitionExecution", partitionExecutionId, metrics, persistentUserData, batchStatus, exitStatus, timestamp), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        return transport.invokeRemote(address, this.<Set<String>>_cmd("getJobNames"), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        return transport.invokeRemote(address, this.<Integer>_cmd("getJobInstanceCount", jobName), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        return transport.invokeRemote(address, this.<List<JobInstance>>_cmd("getJobInstances", jobName, start, count), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        return transport.invokeRemote(address, this.<List<Long>>_cmd("getRunningExecutions", jobName), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<Properties>_cmd("getParameters", jobExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedJobInstance>_cmd("getJobInstance", jobInstanceId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedJobInstance>_cmd("getJobInstanceForExecution", jobExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        return transport.invokeRemote(address, this.<List<? extends JobExecution>>_cmd("getJobExecutions", jobInstanceId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedJobExecution>_cmd("getJobExecution", jobExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedJobExecution>_cmd("restartJobExecution", jobExecutionId, parameters), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<List<? extends StepExecution>>_cmd("getStepExecutionsForJobExecution", jobExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("getStepExecution", stepExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("getPreviousStepExecution", jobExecutionId, stepExecutionId, stepName), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        return transport.invokeRemote(address, this.<ExtendedStepExecution>_cmd("getLatestStepExecution", jobExecutionId, stepName), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        return transport.invokeRemote(address, this.<Integer>_cmd("getStepExecutionCount", jobExecutionId, stepName), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        return transport.invokeRemote(address, this.<StepExecution[]>_cmd("getStepExecutions", new Serializable[]{stepExecutionIds}), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<PartitionExecution[]>_cmd("getUnfinishedPartitionExecutions", stepExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        return transport.invokeRemote(address, this.<PartitionExecution>_cmd("getPartitionExecution", partitionExecutionId), this.timeout, this.unit)
                .get(timeout, unit);
    }
}
