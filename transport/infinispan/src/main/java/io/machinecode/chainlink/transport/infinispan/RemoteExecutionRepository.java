package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.transport.infinispan.cmd.InvokeExecutionRepositoryCommand;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import org.infinispan.remoting.transport.Address;

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

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RemoteExecutionRepository implements ExecutionRepository {

    protected final InfinispanTransport executor;
    protected final Address address;
    protected final ExecutionRepositoryId executionRepositoryId;

    public RemoteExecutionRepository(final InfinispanTransport executor, final ExecutionRepositoryId executionRepositoryId, final Address address) {
        this.executor = executor;
        this.address = address;
        this.executionRepositoryId = executionRepositoryId;
    }

    private InvokeExecutionRepositoryCommand _cmd(final String name, final Object... params) {
        return new InvokeExecutionRepositoryCommand(executor.cacheName, address, executionRepositoryId, name, params);
    }

    @Override
    public ExtendedJobInstance createJobInstance(final Job job, final String jslName, final Date timestamp) throws Exception {
        return (ExtendedJobInstance)executor.invokeSync(address, _cmd("createJobInstance", job, jslName, timestamp));
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        return (ExtendedJobExecution)executor.invokeSync(address, _cmd("createJobExecution", jobInstance, parameters, timestamp));
    }

    @Override
    public ExtendedStepExecution createStepExecution(final JobExecution jobExecution, final String stepName, final Date timestamp) throws Exception {
        return (ExtendedStepExecution)executor.invokeSync(address, _cmd("createStepExecution", jobExecution, stepName, timestamp));
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        return (PartitionExecution)executor.invokeSync(address, _cmd("createPartitionExecution", stepExecutionId, partitionId, properties, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp));
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("startJobExecution", jobExecutionId, timestamp));
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("updateJobExecution", jobExecutionId, batchStatus, timestamp));
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("finishJobExecution", jobExecutionId, batchStatus, exitStatus, restartElementId, timestamp));
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        executor.invokeSync(address, _cmd("linkJobExecutions", jobExecutionId, restartJobExecutionId));
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("startStepExecution", stepExecutionId, timestamp));
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, timestamp));
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp));
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("finishStepExecution", stepExecutionId, metrics, batchStatus, exitStatus, timestamp));
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("startPartitionExecution", partitionExecutionId, timestamp));
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("updatePartitionExecution", partitionExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp));
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        executor.invokeSync(address, _cmd("finishPartitionExecution", partitionExecutionId, metrics, persistentUserData, batchStatus, exitStatus, timestamp));
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        return (Set<String>)executor.invokeSync(address, _cmd("getJobNames"));
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        return (Integer)executor.invokeSync(address, _cmd("getJobInstanceCount", jobName));
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        return (List<JobInstance>)executor.invokeSync(address, _cmd("getJobInstances", jobName, start, count));
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        return (List<Long>)executor.invokeSync(address, _cmd("getRunningExecutions", jobName));
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        return (Properties)executor.invokeSync(address, _cmd("getParameters", jobExecutionId));
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        return (ExtendedJobInstance)executor.invokeSync(address, _cmd("getJobInstance", jobInstanceId));
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        return (ExtendedJobInstance)executor.invokeSync(address, _cmd("getJobInstanceForExecution", jobExecutionId));
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final JobInstance instance) throws Exception {
        return (List<? extends JobExecution>)executor.invokeSync(address, _cmd("getJobExecutions", instance));
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        return (ExtendedJobExecution)executor.invokeSync(address, _cmd("getJobExecution", jobExecutionId));
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        return (ExtendedJobExecution)executor.invokeSync(address, _cmd("restartJobExecution", jobExecutionId, parameters));
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        return (List<? extends StepExecution>)executor.invokeSync(address, _cmd("getStepExecutionsForJobExecution", jobExecutionId));
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        return (ExtendedStepExecution)executor.invokeSync(address, _cmd("getStepExecution", stepExecutionId));
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        return (ExtendedStepExecution)executor.invokeSync(address, _cmd("getPreviousStepExecution", jobExecutionId, stepExecutionId, stepName));
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        return (ExtendedStepExecution)executor.invokeSync(address, _cmd("getLatestStepExecution", jobExecutionId, stepName));
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        return (Integer)executor.invokeSync(address, _cmd("getStepExecutionCount", jobExecutionId, stepName));
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        return (StepExecution[])executor.invokeSync(address, _cmd("getStepExecutions", stepExecutionIds));
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        return (PartitionExecution[])executor.invokeSync(address, _cmd("getPartitionExecution", stepExecutionId));
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        return (PartitionExecution)executor.invokeSync(address, _cmd("getPartitionExecution", partitionExecutionId));
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
