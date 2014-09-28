package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.transport.jgroups.cmd.InvokeExecutionRepositoryCommand;
import io.machinecode.then.core.PromiseImpl;
import org.jgroups.Address;

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
public class JGroupsRemoteExecutionRepository implements ExecutionRepository {

    protected final JGroupsRegistry registry;
    protected final Address address;
    protected final ExecutionRepositoryId executionRepositoryId;

    public JGroupsRemoteExecutionRepository(final JGroupsRegistry registry, final ExecutionRepositoryId executionRepositoryId, final Address address) {
        this.registry = registry;
        this.address = address;
        this.executionRepositoryId = executionRepositoryId;
    }

    private InvokeExecutionRepositoryCommand _cmd(final String name, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand(executionRepositoryId, name, params);
    }

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<ExtendedJobInstance,Throwable> promise = new PromiseImpl<ExtendedJobInstance,Throwable>();
            registry.invoke(address, _cmd("createJobInstance", jobId, jslName, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<ExtendedJobExecution,Throwable> promise = new PromiseImpl<ExtendedJobExecution,Throwable>();
            registry.invoke(address, _cmd("createJobExecution", jobInstance, parameters, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution createStepExecution(final ExtendedJobExecution jobExecution, final String stepName, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<ExtendedStepExecution,Throwable> promise = new PromiseImpl<ExtendedStepExecution,Throwable>();
            registry.invoke(address, _cmd("createStepExecution", jobExecution, stepName, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<PartitionExecution,Throwable> promise = new PromiseImpl<PartitionExecution,Throwable>();
            registry.invoke(address, _cmd("createPartitionExecution", stepExecutionId, partitionId, properties, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("startJobExecution", jobExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("updateJobExecution", jobExecutionId, batchStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("finishJobExecution", jobExecutionId, batchStatus, exitStatus, restartElementId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("linkJobExecutions", jobExecutionId, restartJobExecutionId), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("startStepExecution", stepExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("updateStepExecution", stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("finishStepExecution", stepExecutionId, metrics, batchStatus, exitStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("startPartitionExecution", partitionExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("updatePartitionExecution", partitionExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        try {
            final PromiseImpl<Void,Throwable> promise = new PromiseImpl<Void,Throwable>();
            registry.invoke(address, _cmd("finishPartitionExecution", partitionExecutionId, metrics, persistentUserData, batchStatus, exitStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        try {
            final PromiseImpl<Set<String>,Throwable> promise = new PromiseImpl<Set<String>,Throwable>();
            registry.invoke(address, _cmd("getJobNames"), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        try {
            final PromiseImpl<Integer,Throwable> promise = new PromiseImpl<Integer,Throwable>();
            registry.invoke(address, _cmd("getJobInstanceCount", jobName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        try {
            final PromiseImpl<List<JobInstance>,Throwable> promise = new PromiseImpl<List<JobInstance>,Throwable>();
            registry.invoke(address, _cmd("getJobInstances", jobName, start, count), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        try {
            final PromiseImpl<List<Long>,Throwable> promise = new PromiseImpl<List<Long>,Throwable>();
            registry.invoke(address, _cmd("getRunningExecutions", jobName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        try {
            final PromiseImpl<Properties,Throwable> promise = new PromiseImpl<Properties,Throwable>();
            registry.invoke(address, _cmd("getParameters", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        try {
            final PromiseImpl<ExtendedJobInstance,Throwable> promise = new PromiseImpl<ExtendedJobInstance,Throwable>();
            registry.invoke(address, _cmd("getJobInstance", jobInstanceId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        try {
            final PromiseImpl<ExtendedJobInstance,Throwable> promise = new PromiseImpl<ExtendedJobInstance,Throwable>();
            registry.invoke(address, _cmd("getJobInstanceForExecution", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        try {
            final PromiseImpl<List<? extends JobExecution>,Throwable> promise = new PromiseImpl<List<? extends JobExecution>,Throwable>();
            registry.invoke(address, _cmd("getJobExecutions", jobInstanceId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        try {
            final PromiseImpl<ExtendedJobExecution,Throwable> promise = new PromiseImpl<ExtendedJobExecution,Throwable>();
            registry.invoke(address, _cmd("getJobExecution", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        try {
            final PromiseImpl<ExtendedJobExecution,Throwable> promise = new PromiseImpl<ExtendedJobExecution,Throwable>();
            registry.invoke(address, _cmd("restartJobExecution", jobExecutionId, parameters), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        try {
            final PromiseImpl<List<? extends StepExecution>,Throwable> promise = new PromiseImpl<List<? extends StepExecution>,Throwable>();
            registry.invoke(address, _cmd("getStepExecutionsForJobExecution", jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        try {
            final PromiseImpl<ExtendedStepExecution,Throwable> promise = new PromiseImpl<ExtendedStepExecution,Throwable>();
            registry.invoke(address, _cmd("getStepExecution", stepExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        try {
            final PromiseImpl<ExtendedStepExecution,Throwable> promise = new PromiseImpl<ExtendedStepExecution,Throwable>();
            registry.invoke(address, _cmd("getPreviousStepExecution", jobExecutionId, stepExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        try {
            final PromiseImpl<ExtendedStepExecution,Throwable> promise = new PromiseImpl<ExtendedStepExecution,Throwable>();
            registry.invoke(address, _cmd("getLatestStepExecution", jobExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        try {
            final PromiseImpl<Integer,Throwable> promise = new PromiseImpl<Integer,Throwable>();
            registry.invoke(address, _cmd("getStepExecutionCount", jobExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        try {
            final PromiseImpl<StepExecution[],Throwable> promise = new PromiseImpl<StepExecution[],Throwable>();
            registry.invoke(address, _cmd("getStepExecutions", new Serializable[]{ stepExecutionIds }), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        try {
            final PromiseImpl<PartitionExecution[],Throwable> promise = new PromiseImpl<PartitionExecution[],Throwable>();
            registry.invoke(address, _cmd("getUnfinishedPartitionExecutions", stepExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        try {
            final PromiseImpl<PartitionExecution,Throwable> promise = new PromiseImpl<PartitionExecution,Throwable>();
            registry.invoke(address, _cmd("getPartitionExecution", partitionExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isLocal() {
        return false;
    }
}
