package io.machinecode.chainlink.repository.memory;

import gnu.trove.impl.Constants;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLongHashSet;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.context.MutableMetric;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Step;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.repository.core.JobExecutionImpl;
import io.machinecode.chainlink.repository.core.JobInstanceImpl;
import io.machinecode.chainlink.repository.core.MutableMetricImpl;
import io.machinecode.chainlink.repository.core.PartitionExecutionImpl;
import io.machinecode.chainlink.repository.core.StepExecutionImpl;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobSecurityException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@SuppressWarnings("ALL")
public class MemoryExecutionRepository implements ExecutionRepository {

    private static final long FIRST_INDEX = Constants.DEFAULT_LONG_NO_ENTRY_VALUE + 1;

    protected final AtomicLong jobInstanceIndex = new AtomicLong(FIRST_INDEX);
    protected final AtomicLong jobExecutionIndex = new AtomicLong(FIRST_INDEX);
    protected final AtomicLong stepExecutionIndex = new AtomicLong(FIRST_INDEX);
    protected final AtomicLong partitionExecutionIndex = new AtomicLong(FIRST_INDEX);

    protected final TLongObjectMap<ExtendedJobInstance> jobInstances = new TLongObjectHashMap<ExtendedJobInstance>();
    protected final TLongObjectMap<ExtendedJobExecution> jobExecutions = new TLongObjectHashMap<ExtendedJobExecution>();
    protected final TLongObjectMap<ExtendedStepExecution> stepExecutions = new TLongObjectHashMap<ExtendedStepExecution>();
    protected final TLongObjectMap<PartitionExecution> partitionExecutions = new TLongObjectHashMap<PartitionExecution>();
    protected final TLongObjectMap<TLongList> jobInstanceExecutions = new TLongObjectHashMap<TLongList>();
    protected final TLongLongMap jobExecutionInstances = new TLongLongHashMap();
    protected final TLongObjectMap<TLongSet> jobExecutionStepExecutions = new TLongObjectHashMap<TLongSet>();
    protected final TLongLongMap latestJobExecutionForInstance = new TLongLongHashMap();
    protected final TLongObjectMap<TLongList> stepExecutionPartitionExecutions = new TLongObjectHashMap<TLongList>();
    protected final TLongObjectMap<TLongSet> jobExecutionHistory = new TLongObjectHashMap<TLongSet>();

    protected final AtomicBoolean jobInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean stepExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean partitionExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobInstanceExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionStepExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean latestJobExecutionForInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean stepExecutionPartitionExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionHistoryLock = new AtomicBoolean(false);

    @Override
    public JobInstanceImpl createJobInstance(final Job job, final String jslName) {
        final long id = jobInstanceIndex.getAndIncrement();
        final JobInstanceImpl instance = new JobInstanceImpl.Builder()
                .setInstanceId(id)
                .setJobName(job.getId())
                .setJslName(jslName)
                .build();
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            jobInstances.put(id, instance);
        } finally {
            jobInstanceLock.set(false);
        }
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            jobInstanceExecutions.put(id, new TLongArrayList(0));
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        return instance;
    }

    @Override
    public JobExecutionImpl createJobExecution(final ExtendedJobInstance instance, final Properties parameters, final Date timestamp) throws NoSuchJobInstanceException {
        final long jobExecutionId;
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            final TLongList executions = jobInstanceExecutions.get(instance.getInstanceId());
            if (executions == null) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", instance.getInstanceId()));
            }
            jobExecutionId = jobExecutionIndex.getAndIncrement();
            executions.add(jobExecutionId);
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setExecutionId(jobExecutionId)
                .setJobName(instance.getJobName())
                .setBatchStatus(BatchStatus.STARTING)
                .setParameters(parameters)
                .setCreated(timestamp)
                .setUpdated(timestamp)
                .build();
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            jobExecutions.put(jobExecutionId, execution);
        } finally {
            jobExecutionLock.set(false);
        }
        while (!jobExecutionInstanceLock.compareAndSet(false, true)) {}
        try {
            jobExecutionInstances.put(jobExecutionId, instance.getInstanceId());
        } finally {
            jobExecutionInstanceLock.set(false);
        }
        while (!latestJobExecutionForInstanceLock.compareAndSet(false, true)) {}
        try {
            latestJobExecutionForInstance.put(instance.getInstanceId(), jobExecutionId);
        } finally {
            latestJobExecutionForInstanceLock.set(false);
        }
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            jobExecutionStepExecutions.put(jobExecutionId, new TLongHashSet(0));
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        while (!jobExecutionHistoryLock.compareAndSet(false, true)) {}
        try {
            jobExecutionHistory.put(jobExecutionId, new TLongHashSet(0));
        } finally {
            jobExecutionHistoryLock.set(false);
        }
        return execution;
    }

    @Override
    public StepExecutionImpl createStepExecution(final JobExecution jobExecution, final Step<?, ?> step, final Date timestamp) throws Exception {
        final long stepExecutionId;
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            final TLongSet executionIds = jobExecutionStepExecutions.get(jobExecution.getExecutionId());
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecution.getExecutionId()));
            }
            stepExecutionId = stepExecutionIndex.getAndIncrement();
            executionIds.add(stepExecutionId);
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        final StepExecutionImpl execution = new StepExecutionImpl.Builder()
                .setStepExecutionId(stepExecutionId)
                .setStepName(step.getId())
                .setCreated(timestamp)
                .setUpdated(timestamp)
                .setBatchStatus(BatchStatus.STARTING)
                .setMetrics(MutableMetricImpl.empty())
                .build();
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            stepExecutions.putIfAbsent(stepExecutionId, execution);
            //final StepExecution old = stepExecutions.putIfAbsent(id, execution);
            //if (old != null) {
            //    throw new StepAlreadyExistsException();
            //}
        } finally {
            stepExecutionLock.set(false);
        }
        while (!stepExecutionPartitionExecutionLock.compareAndSet(false, true)) {}
        try {
            stepExecutionPartitionExecutions.put(stepExecutionId, new TLongArrayList());
        } finally {
            stepExecutionPartitionExecutionLock.set(false);
        }
        return execution;
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Date timestamp) throws Exception {
        final long id;
        while (!stepExecutionPartitionExecutionLock.compareAndSet(false, true)) {}
        try {
            TLongList partitions = stepExecutionPartitionExecutions.get(stepExecutionId);
            if (partitions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            id = partitionExecutionIndex.getAndIncrement();
            partitions.add(id);
        } finally {
            stepExecutionPartitionExecutionLock.set(false);
        }
        final PartitionExecutionImpl execution = new PartitionExecutionImpl.Builder()
                .setPartitionExecutionId(id)
                .setStepExecutionId(stepExecutionId)
                .setPartitionId(partitionId)
                .setPartitionProperties(properties)
                .setCreated(timestamp)
                .setUpdated(timestamp)
                .setMetrics(MutableMetricImpl.empty())
                .setBatchStatus(BatchStatus.STARTING)
                .build();
        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            partitionExecutions.put(id, execution);
        } finally {
            partitionExecutionLock.set(false);
        }
        return execution;
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final PartitionExecution partitionExecution, final Date timestamp) throws Exception {
        final long id;
        while (!stepExecutionPartitionExecutionLock.compareAndSet(false, true)) {}
        try {
            TLongList partitions = stepExecutionPartitionExecutions.get(stepExecutionId);
            if (partitions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            id = partitionExecutionIndex.getAndIncrement();
            partitions.add(id);
        } finally {
            stepExecutionPartitionExecutionLock.set(false);
        }
        final PartitionExecutionImpl execution = PartitionExecutionImpl.from(partitionExecution)
                .setPartitionExecutionId(id)
                .setStepExecutionId(stepExecutionId)
                .setCreated(timestamp)
                .setUpdated(timestamp)
                .setStart(null)
                .setEnd(null)
                .setMetrics(MutableMetricImpl.empty())
                .setBatchStatus(BatchStatus.STARTING)
                .setExitStatus(null)
                .build();
        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            partitionExecutions.put(id, execution);
        } finally {
            partitionExecutionLock.set(false);
        }
        return execution;
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setStart(timestamp)
                    .setBatchStatus(BatchStatus.STARTED)
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setBatchStatus(batchStatus)
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setRestartElementId(restartElementId)
                    .setUpdated(timestamp)
                    .setEnd(timestamp)
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final ExtendedJobExecution restartJobExecution) throws NoSuchJobExecutionException, JobSecurityException {
        final long restartJobExecutionId = restartJobExecution.getExecutionId();
        final TLongSet jobExecutionIds;
        final TLongSet oldJobExecutionIds;
        while (!jobExecutionHistoryLock.compareAndSet(false, true)) {}
        try {
            oldJobExecutionIds = jobExecutionHistory.get(restartJobExecutionId);
            if (oldJobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", restartJobExecution));
            }
            jobExecutionIds = jobExecutionHistory.get(jobExecutionId);
            if (jobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutionIds.add(restartJobExecutionId);
            jobExecutionIds.addAll(oldJobExecutionIds);
        } finally {
            jobExecutionHistoryLock.set(false);
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setStart(timestamp)
                    .setUpdated(timestamp)
                    .setBatchStatus(BatchStatus.STARTED)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Serializable persistentUserData, final Metric[] metrics, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        final Serializable _persistentUserData = _clone(persistentUserData);
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setPersistentUserData(_persistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Serializable persistentUserData, final Metric[] metrics, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        final Serializable _persistentUserData = _clone(persistentUserData);
        final Serializable _reader = _clone(readerCheckpoint);
        final Serializable _writer = _clone(writerCheckpoint);
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setPersistentUserData(_persistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .setReaderCheckpoint(_reader)
                    .setWriterCheckpoint(_writer)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus, final Metric[] metrics, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setUpdated(timestamp)
                    .setEnd(timestamp)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        final Serializable _persistentUserData = _clone(persistentUserData);
        final Serializable _reader = _clone(readerCheckpoint);
        final Serializable _writer = _clone(writerCheckpoint);
        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                    .setUpdated(timestamp)
                    .setReaderCheckpoint(_reader)
                    .setWriterCheckpoint(_writer)
                    .setPersistentUserData(_persistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            partitionExecutionLock.set(false);
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Serializable persistentUserData, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {        final Serializable _reader;
        final Serializable _persistentUserData = _clone(persistentUserData);
        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                    .setUpdated(timestamp)
                    .setBatchStatus(batchStatus)
                    .setPersistentUserData(_persistentUserData)
                    .build()
            );
        } finally {
            partitionExecutionLock.set(false);
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        final Serializable _persistentUserData = _clone(persistentUserData);
        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                    .setEnd(timestamp)
                    .setUpdated(timestamp)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setPersistentUserData(_persistentUserData)
                    .build()
            );
        } finally {
            partitionExecutionLock.set(false);
        }
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        final Set<String> ret = new THashSet<String>();
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            for (final JobInstance instance : jobInstances.valueCollection()) {
                ret.add(instance.getJobName());
            }
        } finally {
            jobInstanceLock.set(false);
        }
        return ret;
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            int ret = 0;
            for (final JobInstance instance : jobInstances.valueCollection()) {
                if (jobName.equals(instance.getJobName())) {
                    ++ret;
                }
            }
            if (ret == 0) {
                 throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            return ret;
        } finally {
            jobInstanceLock.set(false);
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        final List<JobInstance> ret = new ArrayList<JobInstance>(count);
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            for (final JobInstance instance : jobInstances.valueCollection()) {
                if (jobName.equals(instance.getJobName())) {
                    ret.add(instance);
                }
            }
        } finally {
            jobInstanceLock.set(false);
        }
        if (ret.isEmpty()) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
        }
        Collections.reverse(ret);
        final int size = ret.size();
        if (start >= size) {
            return Collections.emptyList();
        }
        if (start + count > size) {
            return Collections.emptyList();
        }
        return ret.subList(start, start + count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<Long> ids = new ArrayList<Long>();
            for (final JobExecution execution : jobExecutions.valueCollection()) {
                if (jobName.equals(execution.getJobName())) {
                    ids.add(execution.getExecutionId());
                }
            }
            if (ids.isEmpty()) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            return ids;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final JobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return execution.getJobParameters();
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            final ExtendedJobInstance instance = jobInstances.get(jobInstanceId);
            if (instance == null) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
            }
            return instance;
        } finally {
            jobInstanceLock.set(false);
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final long instanceId;
        while (!jobExecutionInstanceLock.compareAndSet(false, true)) {}
        try {
            instanceId = jobExecutionInstances.get(jobExecutionId);
            if (instanceId == jobExecutionInstances.getNoEntryValue()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
        } finally {
            jobExecutionInstanceLock.set(false);
        }
        return getJobInstance(instanceId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        final TLongList jobExecutionIds = new TLongArrayList();
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            final TLongList executions = jobInstanceExecutions.get(instance.getInstanceId());
            if (executions == null) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", instance.getInstanceId()));
            }
            jobExecutionIds.addAll(executions);
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<JobExecution> executions = new ArrayList<JobExecution>(jobExecutionIds.size());
            for (final TLongIterator it = jobExecutionIds.iterator(); it.hasNext();) {
                final long executionId = it.next();
                executions.add(jobExecutions.get(executionId));
            }
            return executions;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return execution;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException {
        final long instanceId;
        while (!jobExecutionInstanceLock.compareAndSet(false, true)) {}
        try {
            instanceId = jobExecutionInstances.get(jobExecutionId);
            if (instanceId == jobExecutionInstances.getNoEntryValue()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
        } finally {
            jobExecutionInstanceLock.set(false);
        }
        final long latest;
        while (!latestJobExecutionForInstanceLock.compareAndSet(false, true)) {}
        try {
            latest = latestJobExecutionForInstance.get(instanceId);
            if (latest == latestJobExecutionForInstance.getNoEntryValue()) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobExecutionId));
            }
            if (latest != jobExecutionId) {
                throw new JobExecutionNotMostRecentException(Messages.format("CHAINLINK-006004.repository.not.most.recent.execution", jobExecutionId, instanceId));
            }
        } finally {
            latestJobExecutionForInstanceLock.set(false);
        }
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final JobExecution execution = jobExecutions.get(latest);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            switch (execution.getBatchStatus()) {
                case STOPPED:
                case FAILED:
                    break;
                case COMPLETED:
                    throw new JobExecutionAlreadyCompleteException(Messages.format("CHAINLINK-006006.execution.repository.execution.already.complete", jobExecutionId));
                default:
                    throw new JobRestartException(Messages.format("CHAINLINK-006007.execution.repository.execution.not.eligible.for.restart", execution.getExecutionId(), BatchStatus.STOPPED, BatchStatus.FAILED, execution.getBatchStatus()));
            }
        } finally {
            jobExecutionLock.set(false);
        }
        return createJobExecution(
                getJobInstance(instanceId),
                parameters,
                new Date()
        );
    }

    @Override
    public List<StepExecution> getStepExecutionsForJob(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet stepExecutionIds = new TLongHashSet();
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            final TLongSet executions = jobExecutionStepExecutions.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executions);
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<StepExecution> stepExecutions = new ArrayList<StepExecution>(stepExecutionIds.size());
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                stepExecutions.add(this.stepExecutions.get(it.next()));
            }
            return stepExecutions;
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        while (!jobExecutionHistoryLock.compareAndSet(false, true)) {}
        try {
            final TLongSet executions = jobExecutionHistory.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            historicJobExecutionIds.addAll(executions);
        } finally {
            jobExecutionHistoryLock.set(false);
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            TLongSet executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
                final long historicJobExecutionId = it.next();
                executionIds = jobExecutionStepExecutions.get(historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", historicJobExecutionId));
                }
                stepExecutionIds.addAll(executionIds);
            }
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>();
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final long id = it.next();
                if (stepExecutionId == id) {
                    continue;
                }
                final ExtendedStepExecution execution = stepExecutions.get(id);
                if (stepName.equals(execution.getStepName())) {
                    candidates.add(execution);
                }
            }
        } finally {
            stepExecutionLock.set(false);
        }
        ExtendedStepExecution latest = null;
        for (final ExtendedStepExecution candidate : candidates) {
            if (latest == null) {
                latest = candidate;
                continue;
            }
            if (candidate.getStartTime().after(latest.getStartTime())) {
                latest = candidate;
            }
        }
        if (latest == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        while (!jobExecutionHistoryLock.compareAndSet(false, true)) {}
        try {
            final TLongSet executions = jobExecutionHistory.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            historicJobExecutionIds.addAll(executions);
        } finally {
            jobExecutionHistoryLock.set(false);
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            TLongSet executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
                final long historicJobExecutionId = it.next();
                executionIds = jobExecutionStepExecutions.get(historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", historicJobExecutionId));
                }
                stepExecutionIds.addAll(executionIds);
            }
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>();
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final ExtendedStepExecution execution = stepExecutions.get(it.next());
                if (stepName.equals(execution.getStepName())) {
                    candidates.add(execution);
                }
            }
        } finally {
            stepExecutionLock.set(false);
        }
        ExtendedStepExecution latest = null;
        for (final ExtendedStepExecution candidate : candidates) {
            if (latest == null) {
                latest = candidate;
                continue;
            }
            if (candidate.getStartTime().after(latest.getStartTime())) {
                latest = candidate;
            }
        }
        if (latest == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        while (!jobExecutionHistoryLock.compareAndSet(false, true)) {}
        try {
            final TLongSet executions = jobExecutionHistory.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            historicJobExecutionIds.addAll(executions);
        } finally {
            jobExecutionHistoryLock.set(false);
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            TLongSet executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
                final long historicJobExecutionId = it.next();
                executionIds = jobExecutionStepExecutions.get(historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", historicJobExecutionId));
                }
                stepExecutionIds.addAll(executionIds);
            }
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>(stepExecutionIds.size());
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final long stepExecutionId = it.next();
                final ExtendedStepExecution stepExecution = stepExecutions.get(stepExecutionId);
                if (stepExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
                }
                if (stepName.equals(stepExecution.getStepName())) {
                    candidates.add(stepExecution);
                }
            }
        } finally {
            stepExecutionLock.set(false);
        }
        return candidates.size();
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            return stepExecutions.get(stepExecutionId);
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws NoSuchJobExecutionException, JobSecurityException {
        final StepExecution[] executions = new StepExecution[stepExecutionIds.length];
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            for (int i = 0; i < stepExecutionIds.length; ++i) {
                executions[i] = stepExecutions.get(stepExecutionIds[i]);
            }
            return executions;
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongList partitionIds;
        while (!stepExecutionPartitionExecutionLock.compareAndSet(false, true)) {}
        try {
            partitionIds = stepExecutionPartitionExecutions.get(stepExecutionId);
            if (partitionIds.isEmpty()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
        } finally {
            stepExecutionPartitionExecutionLock.set(false);
        }


        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<PartitionExecution> ret = new ArrayList<PartitionExecution>();
            for (final TLongIterator it = partitionIds.iterator(); it.hasNext();) {
                final long partitionExecutionId = it.next();
                final PartitionExecution partitionExecution = partitionExecutions.get(partitionExecutionId);
                if (partitionExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
                }
                switch (partitionExecution.getBatchStatus()) {
                    case FAILED:
                    case STOPPED:
                    case STOPPING:
                    case STARTED:
                    case STARTING:
                        ret.add(partitionExecution);
                        continue;
                    case ABANDONED:
                        throw new IllegalStateException(); //TODO Message
                }
            }
            return ret.toArray(new PartitionExecution[ret.size()]);
        } finally {
            partitionExecutionLock.set(false);
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!partitionExecutionLock.compareAndSet(false, true)) {}
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            return partition;
        } finally {
            partitionExecutionLock.set(false);
        }
    }

    //TODO Add recycling
    private <T> T _clone(final T that) {
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(that);
            oos.flush();
            return (T) new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray())).readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e); //TODO
        } catch (IOException e) {
            throw new RuntimeException(e); //TODO
        }
    }
}
