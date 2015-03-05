package io.machinecode.chainlink.core.repository.memory;

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
import io.machinecode.chainlink.core.repository.JobExecutionImpl;
import io.machinecode.chainlink.core.repository.JobInstanceImpl;
import io.machinecode.chainlink.core.repository.MutableMetricImpl;
import io.machinecode.chainlink.core.repository.PartitionExecutionImpl;
import io.machinecode.chainlink.core.repository.StepExecutionImpl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.repository.Repository;

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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@SuppressWarnings("ALL")
public class MemoryRepository implements Repository {

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

    protected final ReadWriteLock jobInstanceLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock jobExecutionLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock stepExecutionLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock partitionExecutionLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock jobInstanceExecutionLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock jobExecutionInstanceLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock jobExecutionStepExecutionLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock latestJobExecutionForInstanceLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock stepExecutionPartitionExecutionLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock jobExecutionHistoryLock = new ReentrantReadWriteLock();

    protected final Marshalling marshalling;

    public MemoryRepository(final Marshalling marshalling) {
        this.marshalling = marshalling;
    }

    @Override
    public JobInstanceImpl createJobInstance(final String jobId, final String jslName, final Date timestamp) {
        final long id = jobInstanceIndex.getAndIncrement();
        final JobInstanceImpl instance = new JobInstanceImpl.Builder()
                .setJobInstanceId(id)
                .setJobName(jobId)
                .setJslName(jslName)
                .setCreateTime(timestamp)
                .build();
        jobInstanceLock.writeLock().lock();
        try {
            jobInstances.put(id, instance);
        } finally {
            jobInstanceLock.writeLock().unlock();
        }
        jobInstanceExecutionLock.writeLock().lock();
        try {
            jobInstanceExecutions.put(id, new TLongArrayList(0));
        } finally {
            jobInstanceExecutionLock.writeLock().unlock();
        }
        return instance;
    }

    @Override
    public JobExecutionImpl createJobExecution(final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws NoSuchJobInstanceException {
        final long jobExecutionId;
        jobInstanceExecutionLock.writeLock().lock();
        try {
            final TLongList executions = jobInstanceExecutions.get(jobInstanceId);
            if (executions == null) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
            }
            jobExecutionId = jobExecutionIndex.getAndIncrement();
            executions.add(jobExecutionId);
        } finally {
            jobInstanceExecutionLock.writeLock().unlock();
        }
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setJobInstanceId(jobInstanceId)
                .setJobExecutionId(jobExecutionId)
                .setJobName(jobName)
                .setBatchStatus(BatchStatus.STARTING)
                .setJobParameters(parameters)
                .setCreateTime(timestamp)
                .setLastUpdatedTime(timestamp)
                .build();
        jobExecutionLock.writeLock().lock();
        try {
            jobExecutions.put(jobExecutionId, execution);
        } finally {
            jobExecutionLock.writeLock().unlock();
        }
        jobExecutionInstanceLock.writeLock().lock();
        try {
            jobExecutionInstances.put(jobExecutionId, jobInstanceId);
        } finally {
            jobExecutionInstanceLock.writeLock().unlock();
        }
        latestJobExecutionForInstanceLock.writeLock().lock();
        try {
            latestJobExecutionForInstance.put(jobInstanceId, jobExecutionId);
        } finally {
            latestJobExecutionForInstanceLock.writeLock().unlock();
        }
        jobExecutionStepExecutionLock.writeLock().lock();
        try {
            jobExecutionStepExecutions.put(jobExecutionId, new TLongHashSet(0));
        } finally {
            jobExecutionStepExecutionLock.writeLock().unlock();
        }
        jobExecutionHistoryLock.writeLock().lock();
        try {
            jobExecutionHistory.put(jobExecutionId, new TLongHashSet(0));
        } finally {
            jobExecutionHistoryLock.writeLock().unlock();
        }
        return execution;
    }

    @Override
    public StepExecutionImpl createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception {
        final long stepExecutionId;
        jobExecutionStepExecutionLock.writeLock().lock();
        try {
            final TLongSet executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionId = stepExecutionIndex.getAndIncrement();
            executionIds.add(stepExecutionId);
        } finally {
            jobExecutionStepExecutionLock.writeLock().unlock();
        }
        final StepExecutionImpl execution = new StepExecutionImpl.Builder()
                .setJobExecutionId(jobExecutionId)
                .setStepExecutionId(stepExecutionId)
                .setStepName(stepName)
                .setCreateTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(BatchStatus.STARTING)
                .setMetrics(MutableMetricImpl.empty())
                .build();
        stepExecutionLock.writeLock().lock();
        try {
            stepExecutions.putIfAbsent(stepExecutionId, execution);
            //final StepExecution old = stepExecutions.putIfAbsent(id, execution);
            //if (old != null) {
            //    throw new StepAlreadyExistsException();
            //}
        } finally {
            stepExecutionLock.writeLock().unlock();
        }
        stepExecutionPartitionExecutionLock.writeLock().lock();
        try {
            stepExecutionPartitionExecutions.put(stepExecutionId, new TLongArrayList());
        } finally {
            stepExecutionPartitionExecutionLock.writeLock().unlock();
        }
        return execution;
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final long id;
        stepExecutionPartitionExecutionLock.writeLock().lock();
        try {
            TLongList partitions = stepExecutionPartitionExecutions.get(stepExecutionId);
            if (partitions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            id = partitionExecutionIndex.getAndIncrement();
            partitions.add(id);
        } finally {
            stepExecutionPartitionExecutionLock.writeLock().unlock();
        }
        final PartitionExecutionImpl execution = new PartitionExecutionImpl.Builder()
                .setPartitionExecutionId(id)
                .setStepExecutionId(stepExecutionId)
                .setPartitionId(partitionId)
                .setPartitionParameters(properties)
                .setCreateTime(timestamp)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .setMetrics(MutableMetricImpl.empty())
                .setBatchStatus(BatchStatus.STARTING)
                .build();
        partitionExecutionLock.writeLock().lock();
        try {
            partitionExecutions.put(id, execution);
        } finally {
            partitionExecutionLock.writeLock().unlock();
        }
        return execution;
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        jobExecutionLock.writeLock().lock();
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                    .setLastUpdatedTime(timestamp)
                    .setStartTime(timestamp)
                    .setBatchStatus(BatchStatus.STARTED)
                    .build()
            );
        } finally {
            jobExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        jobExecutionLock.writeLock().lock();
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                    .setLastUpdatedTime(timestamp)
                    .setBatchStatus(batchStatus)
                    .build()
            );
        } finally {
            jobExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        jobExecutionLock.writeLock().lock();
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setRestartElementId(restartElementId)
                    .setLastUpdatedTime(timestamp)
                    .setEndTime(timestamp)
                    .build()
            );
        } finally {
            jobExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet jobExecutionIds;
        final TLongSet oldJobExecutionIds;
        jobExecutionHistoryLock.writeLock().lock();
        try {
            oldJobExecutionIds = jobExecutionHistory.get(restartJobExecutionId);
            if (oldJobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", restartJobExecutionId));
            }
            jobExecutionIds = jobExecutionHistory.get(jobExecutionId);
            if (jobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutionIds.add(restartJobExecutionId);
            jobExecutionIds.addAll(oldJobExecutionIds);
        } finally {
            jobExecutionHistoryLock.writeLock().unlock();
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        stepExecutionLock.writeLock().lock();
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setStartTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setBatchStatus(BatchStatus.STARTED)
                    .build()
            );
        } finally {
            stepExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        stepExecutionLock.writeLock().lock();
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setUpdatedTime(timestamp)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            stepExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        stepExecutionLock.writeLock().lock();
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setUpdatedTime(timestamp)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .setReaderCheckpoint(clonedReaderCheckpoint)
                    .setWriterCheckpoint(clonedWriterCheckpoint)
                    .build()
            );
        } finally {
            stepExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        stepExecutionLock.writeLock().lock();
        try {
            final ExtendedStepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setUpdatedTime(timestamp)
                    .setEndTime(timestamp)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            stepExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        partitionExecutionLock.writeLock().lock();
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                    .setUpdatedTime(timestamp)
                    .setStartTime(timestamp)
                    .setBatchStatus(BatchStatus.STARTED)
                    .build()
            );
        } finally {
            partitionExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        partitionExecutionLock.writeLock().lock();
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                    .setUpdatedTime(timestamp)
                    .setReaderCheckpoint(clonedReaderCheckpoint)
                    .setWriterCheckpoint(clonedWriterCheckpoint)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            partitionExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        partitionExecutionLock.writeLock().lock();
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                    .setEndTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MutableMetricImpl.copy(metrics))
                    .build()
            );
        } finally {
            partitionExecutionLock.writeLock().unlock();
        }
    }

    @Override
    public Set<String> getJobNames() throws JobSecurityException {
        final Set<String> ret = new THashSet<String>();
        jobInstanceLock.readLock().lock();
        try {
            for (final JobInstance instance : jobInstances.valueCollection()) {
                ret.add(instance.getJobName());
            }
        } finally {
            jobInstanceLock.readLock().unlock();
        }
        return ret;
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException, JobSecurityException {
        jobInstanceLock.readLock().lock();
        try {
            int ret = 0;
            for (final JobInstance instance : jobInstances.valueCollection()) {
                if (jobName.equals(instance.getJobName())) {
                    ++ret;
                }
            }
            if (ret == 0) {
                 throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
            }
            return ret;
        } finally {
            jobInstanceLock.readLock().unlock();
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, JobSecurityException {
        final List<JobInstance> ret = new ArrayList<JobInstance>(count);
        jobInstanceLock.readLock().lock();
        try {
            for (final JobInstance instance : jobInstances.valueCollection()) {
                if (jobName.equals(instance.getJobName())) {
                    ret.add(instance);
                }
            }
        } finally {
            jobInstanceLock.readLock().unlock();
        }
        if (ret.isEmpty()) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
        }
        Collections.reverse(ret);
        final int size = ret.size();
        if (start >= size) {
            return Collections.emptyList();
        }
        if (start + count > size) {
            return ret.subList(start, size);
        }
        return ret.subList(start, start + count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        jobExecutionLock.readLock().lock();
        try {
            boolean found = false;
            final List<Long> ids = new ArrayList<Long>();
            for (final JobExecution jobExecution : jobExecutions.valueCollection()) {
                if (jobName.equals(jobExecution.getJobName())) {
                    switch (jobExecution.getBatchStatus()) {
                        case STARTED:
                            ids.add(jobExecution.getExecutionId());
                    }
                    found = true;
                }
            }
            if (!found) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
            }
            return ids;
        } finally {
            jobExecutionLock.readLock().unlock();
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        jobExecutionLock.readLock().lock();
        try {
            final JobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            return execution.getJobParameters();
        } finally {
            jobExecutionLock.readLock().unlock();
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws NoSuchJobExecutionException, JobSecurityException {
        jobInstanceLock.readLock().lock();
        try {
            final ExtendedJobInstance instance = jobInstances.get(jobInstanceId);
            if (instance == null) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
            }
            return instance;
        } finally {
            jobInstanceLock.readLock().unlock();
        }
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final long instanceId;
        jobExecutionInstanceLock.readLock().lock();
        try {
            instanceId = jobExecutionInstances.get(jobExecutionId);
            if (instanceId == jobExecutionInstances.getNoEntryValue()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
        } finally {
            jobExecutionInstanceLock.readLock().unlock();
        }
        return getJobInstance(instanceId);
    }

    @Override
    public List<JobExecution> getJobExecutions(final long jobInstanceId) throws NoSuchJobInstanceException, JobSecurityException {
        final TLongList jobExecutionIds = new TLongArrayList();
        jobInstanceExecutionLock.readLock().lock();
        try {
            final TLongList executions = jobInstanceExecutions.get(jobInstanceId);
            if (executions == null) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
            }
            jobExecutionIds.addAll(executions);
        } finally {
            jobInstanceExecutionLock.readLock().unlock();
        }
        jobExecutionLock.readLock().lock();
        try {
            final List<JobExecution> executions = new ArrayList<JobExecution>(jobExecutionIds.size());
            for (final TLongIterator it = jobExecutionIds.iterator(); it.hasNext();) {
                final long executionId = it.next();
                executions.add(jobExecutions.get(executionId));
            }
            return executions;
        } finally {
            jobExecutionLock.readLock().unlock();
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        jobExecutionLock.readLock().lock();
        try {
            final ExtendedJobExecution execution = jobExecutions.get(jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            return execution;
        } finally {
            jobExecutionLock.readLock().unlock();
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws JobRestartException, NoSuchJobExecutionException, NoSuchJobInstanceException, JobExecutionNotMostRecentException, JobSecurityException {
        final long jobInstanceId;
        jobExecutionInstanceLock.readLock().lock();
        try {
            jobInstanceId = jobExecutionInstances.get(jobExecutionId);
            if (jobInstanceId == jobExecutionInstances.getNoEntryValue()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
        } finally {
            jobExecutionInstanceLock.readLock().unlock();
        }
        final long latest;
        latestJobExecutionForInstanceLock.readLock().lock();
        try {
            latest = latestJobExecutionForInstance.get(jobInstanceId);
            if (latest == latestJobExecutionForInstance.getNoEntryValue()) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
            }
            if (latest != jobExecutionId) {
                throw new JobExecutionNotMostRecentException(Messages.format("CHAINLINK-006004.repository.not.most.recent.execution", jobExecutionId, jobInstanceId));
            }
        } finally {
            latestJobExecutionForInstanceLock.readLock().unlock();
        }
        jobExecutionLock.readLock().lock();
        try {
            final JobExecution execution = jobExecutions.get(latest);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            switch (execution.getBatchStatus()) {
                case STOPPED:
                case FAILED:
                    break;
                case COMPLETED:
                    throw new JobExecutionAlreadyCompleteException(Messages.format("CHAINLINK-006006.repository.execution.already.complete", jobExecutionId));
                default:
                    throw new JobRestartException(Messages.format("CHAINLINK-006007.repository.execution.not.eligible.for.restart", execution.getExecutionId(), BatchStatus.STOPPED, BatchStatus.FAILED, execution.getBatchStatus()));
            }
        } finally {
            jobExecutionLock.readLock().unlock();
        }
        return createJobExecution(
                jobInstanceId,
                getJobInstance(jobInstanceId).getJobName(),
                parameters,
                new Date()
        );
    }

    @Override
    public List<StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet stepExecutionIds = new TLongHashSet();
        jobExecutionStepExecutionLock.readLock().lock();
        try {
            final TLongSet executions = jobExecutionStepExecutions.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executions);
        } finally {
            jobExecutionStepExecutionLock.readLock().unlock();
        }
        stepExecutionLock.readLock().lock();
        try {
            final List<StepExecution> stepExecutions = new ArrayList<StepExecution>(stepExecutionIds.size());
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                stepExecutions.add(this.stepExecutions.get(it.next()));
            }
            return stepExecutions;
        } finally {
            stepExecutionLock.readLock().unlock();
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet stepExecutionIds = _stepExecutionHistory(jobExecutionId);
        Date currentStepExecutionCreateTime = null;
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>();
        stepExecutionLock.readLock().lock();
        try {
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final long id = it.next();
                final ExtendedStepExecution stepExecution = stepExecutions.get(id);
                if (stepExecutionId == id) {
                    if (stepExecution == null) {
                        throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
                    }
                    currentStepExecutionCreateTime = stepExecution.getCreateTime();
                    continue;
                }
                if (stepName.equals(stepExecution.getStepName())) {
                    candidates.add(stepExecution);
                }
            }
        } finally {
            stepExecutionLock.readLock().unlock();
        }
        if (currentStepExecutionCreateTime == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        ExtendedStepExecution latest = null;
        for (final ExtendedStepExecution candidate : candidates) {
            final Date candidateTime = candidate.getCreateTime();
            if (currentStepExecutionCreateTime.before(candidateTime)) {
                continue;
            }
            if (latest == null) {
                latest = candidate;
                continue;
            }
            if (candidateTime.after(latest.getCreateTime())) {
                latest = candidate;
            }
        }
        if (latest == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet stepExecutionIds = _stepExecutionHistory(jobExecutionId);
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>();
        stepExecutionLock.readLock().lock();
        try {
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final ExtendedStepExecution execution = stepExecutions.get(it.next());
                if (stepName.equals(execution.getStepName())) {
                    candidates.add(execution);
                }
            }
        } finally {
            stepExecutionLock.readLock().unlock();
        }
        ExtendedStepExecution latest = null;
        for (final ExtendedStepExecution candidate : candidates) {
            if (latest == null) {
                latest = candidate;
                continue;
            }
            if (candidate.getCreateTime().after(latest.getCreateTime())) {
                latest = candidate;
            }
        }
        if (latest == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    protected TLongSet _stepExecutionHistory(final long jobExecutionId) {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        jobExecutionHistoryLock.readLock().lock();
        try {
            final TLongSet executions = jobExecutionHistory.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            historicJobExecutionIds.addAll(executions);
        } finally {
            jobExecutionHistoryLock.readLock().unlock();
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        jobExecutionStepExecutionLock.readLock().lock();
        try {
            TLongSet executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
                final long historicJobExecutionId = it.next();
                executionIds = jobExecutionStepExecutions.get(historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", historicJobExecutionId));
                }
                stepExecutionIds.addAll(executionIds);
            }
        } finally {
            jobExecutionStepExecutionLock.readLock().unlock();
        }
        return stepExecutionIds;
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        jobExecutionHistoryLock.readLock().lock();
        try {
            final TLongSet executions = jobExecutionHistory.get(jobExecutionId);
            if (executions == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            historicJobExecutionIds.addAll(executions);
        } finally {
            jobExecutionHistoryLock.readLock().unlock();
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        jobExecutionStepExecutionLock.readLock().lock();
        try {
            for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
                final long historicJobExecutionId = it.next();
                TLongSet executionIds = jobExecutionStepExecutions.get(historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", historicJobExecutionId));
                }
                stepExecutionIds.addAll(executionIds);
            }
        } finally {
            jobExecutionStepExecutionLock.readLock().unlock();
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>(stepExecutionIds.size());
        stepExecutionLock.readLock().lock();
        try {
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final long stepExecutionId = it.next();
                final ExtendedStepExecution stepExecution = stepExecutions.get(stepExecutionId);
                if (stepExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
                }
                if (stepName.equals(stepExecution.getStepName())) {
                    candidates.add(stepExecution);
                }
            }
        } finally {
            stepExecutionLock.readLock().unlock();
        }
        return candidates.size();
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        stepExecutionLock.readLock().lock();
        try {
            final ExtendedStepExecution stepExecution = stepExecutions.get(stepExecutionId);
            if (stepExecution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            return stepExecution;
        } finally {
            stepExecutionLock.readLock().unlock();
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws NoSuchJobExecutionException, JobSecurityException {
        final StepExecution[] executions = new StepExecution[stepExecutionIds.length];
        stepExecutionLock.readLock().lock();
        try {
            for (int i = 0; i < stepExecutionIds.length; ++i) {
                final ExtendedStepExecution x = stepExecutions.get(stepExecutionIds[i]);
                if (x == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionIds[i]));
                }
                executions[i] = x;
            }
            return executions;
        } finally {
            stepExecutionLock.readLock().unlock();
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final TLongList partitionIds;
        stepExecutionPartitionExecutionLock.readLock().lock();
        try {
            partitionIds = stepExecutionPartitionExecutions.get(stepExecutionId);
            if (partitionIds.isEmpty()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
        } finally {
            stepExecutionPartitionExecutionLock.readLock().unlock();
        }


        partitionExecutionLock.readLock().lock();
        try {
            final List<PartitionExecution> ret = new ArrayList<PartitionExecution>();
            for (final TLongIterator it = partitionIds.iterator(); it.hasNext();) {
                final long partitionExecutionId = it.next();
                final PartitionExecution partitionExecution = partitionExecutions.get(partitionExecutionId);
                if (partitionExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
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
            partitionExecutionLock.readLock().unlock();
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        partitionExecutionLock.readLock().lock();
        try {
            final PartitionExecution partition = partitionExecutions.get(partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            return partition;
        } finally {
            partitionExecutionLock.readLock().unlock();
        }
    }
}
