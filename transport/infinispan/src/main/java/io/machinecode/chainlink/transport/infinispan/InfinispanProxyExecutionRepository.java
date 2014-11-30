package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.transport.infinispan.cmd.InvokeExecutionRepositoryCommand;
import io.machinecode.then.core.DeferredImpl;
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
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class InfinispanProxyExecutionRepository implements ExecutionRepository {

    protected final InfinispanRegistry registry;
    protected final Address address;
    protected final ExecutionRepositoryId executionRepositoryId;

    public InfinispanProxyExecutionRepository(final InfinispanRegistry registry, final ExecutionRepositoryId executionRepositoryId, final Address address) {
        this.registry = registry;
        this.address = address;
        this.executionRepositoryId = executionRepositoryId;
    }

    private InvokeExecutionRepositoryCommand _cmd(final String name, final boolean willReturn, final Serializable... params) {
        return new InvokeExecutionRepositoryCommand(registry.cacheName, executionRepositoryId, name, willReturn, params);
    }

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<ExtendedJobInstance,Throwable,Void>();
            registry.invoke(address, _cmd("createJobInstance", true, jobId, jslName, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<ExtendedJobExecution,Throwable,Void>();
            registry.invoke(address, _cmd("createJobExecution", true, jobInstance, parameters, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution createStepExecution(final ExtendedJobExecution jobExecution, final String stepName, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, _cmd("createStepExecution", true, jobExecution, stepName, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<PartitionExecution,Throwable,Void> promise = new DeferredImpl<PartitionExecution,Throwable,Void>();
            registry.invoke(address, _cmd("createPartitionExecution", true, stepExecutionId, partitionId, properties, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("startJobExecution", false, jobExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("updateJobExecution", false, jobExecutionId, batchStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("finishJobExecution", false, jobExecutionId, batchStatus, exitStatus, restartElementId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("linkJobExecutions", false, jobExecutionId, restartJobExecutionId), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("startStepExecution", false, stepExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("updateStepExecution", false, stepExecutionId, metrics, persistentUserData, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("updateStepExecution", false, stepExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("finishStepExecution", false, stepExecutionId, metrics, batchStatus, exitStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("startPartitionExecution", false, partitionExecutionId, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("updatePartitionExecution", false, partitionExecutionId, metrics, persistentUserData, readerCheckpoint, writerCheckpoint, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        try {
            final DeferredImpl<Void,Throwable,Void> promise = new DeferredImpl<Void,Throwable,Void>();
            registry.invoke(address, _cmd("finishPartitionExecution", false, partitionExecutionId, metrics, persistentUserData, batchStatus, exitStatus, timestamp), promise);
            promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        try {
            final DeferredImpl<Set<String>,Throwable,Void> promise = new DeferredImpl<Set<String>,Throwable,Void>();
            registry.invoke(address, _cmd("getJobNames", true), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        try {
            final DeferredImpl<Integer,Throwable,Void> promise = new DeferredImpl<Integer,Throwable,Void>();
            registry.invoke(address, _cmd("getJobInstanceCount", true, jobName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        try {
            final DeferredImpl<List<JobInstance>,Throwable,Void> promise = new DeferredImpl<List<JobInstance>,Throwable,Void>();
            registry.invoke(address, _cmd("getJobInstances", true, jobName, start, count), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        try {
            final DeferredImpl<List<Long>,Throwable,Void> promise = new DeferredImpl<List<Long>,Throwable,Void>();
            registry.invoke(address, _cmd("getRunningExecutions", true, jobName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<Properties,Throwable,Void> promise = new DeferredImpl<Properties,Throwable,Void>();
            registry.invoke(address, _cmd("getParameters", true, jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        try {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<ExtendedJobInstance,Throwable,Void>();
            registry.invoke(address, _cmd("getJobInstance", true, jobInstanceId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<ExtendedJobInstance,Throwable,Void> promise = new DeferredImpl<ExtendedJobInstance,Throwable,Void>();
            registry.invoke(address, _cmd("getJobInstanceForExecution", true, jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<? extends JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        try {
            final DeferredImpl<List<? extends JobExecution>,Throwable,Void> promise = new DeferredImpl<List<? extends JobExecution>,Throwable,Void>();
            registry.invoke(address, _cmd("getJobExecutions", true, jobInstanceId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<ExtendedJobExecution,Throwable,Void>();
            registry.invoke(address, _cmd("getJobExecution", true, jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        try {
            final DeferredImpl<ExtendedJobExecution,Throwable,Void> promise = new DeferredImpl<ExtendedJobExecution,Throwable,Void>();
            registry.invoke(address, _cmd("restartJobExecution", true, jobExecutionId, parameters), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        try {
            final DeferredImpl<List<? extends StepExecution>,Throwable,Void> promise = new DeferredImpl<List<? extends StepExecution>,Throwable,Void>();
            registry.invoke(address, _cmd("getStepExecutionsForJobExecution", true, jobExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, _cmd("getStepExecution", true, stepExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, _cmd("getPreviousStepExecution", true, jobExecutionId, stepExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        try {
            final DeferredImpl<ExtendedStepExecution,Throwable,Void> promise = new DeferredImpl<ExtendedStepExecution,Throwable,Void>();
            registry.invoke(address, _cmd("getLatestStepExecution", true, jobExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        try {
            final DeferredImpl<Integer,Throwable,Void> promise = new DeferredImpl<Integer,Throwable,Void>();
            registry.invoke(address, _cmd("getStepExecutionCount", true, jobExecutionId, stepName), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        try {
            final DeferredImpl<StepExecution[],Throwable,Void> promise = new DeferredImpl<StepExecution[],Throwable,Void>();
            registry.invoke(address, _cmd("getStepExecutions", true, new Serializable[]{ stepExecutionIds }), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        try {
            final DeferredImpl<PartitionExecution[],Throwable,Void> promise = new DeferredImpl<PartitionExecution[],Throwable,Void>();
            registry.invoke(address, _cmd("getUnfinishedPartitionExecutions", true, stepExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        try {
            final DeferredImpl<PartitionExecution,Throwable,Void> promise = new DeferredImpl<PartitionExecution,Throwable,Void>();
            registry.invoke(address, _cmd("getPartitionExecution", true, partitionExecutionId), promise);
            return promise.get();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }
}
