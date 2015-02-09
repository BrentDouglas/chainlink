package io.machinecode.chainlink.core.repository;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class BaseMapRepository implements Repository {

    protected static final String JOB_INSTANCE_ID = "job_instance_id";
    protected static final String JOB_EXECUTION_ID = "job_execution_id";
    protected static final String STEP_EXECUTION_ID = "step_execution_id";
    protected static final String PARTITION_EXECUTION_ID = "partition_execution_id";

    protected final Marshalling marshalling;

    public BaseMapRepository(final Marshalling marshalling) {
        this.marshalling = marshalling;
    }

    protected abstract Map<String, Long> ids();
    protected abstract Map<Long, ExtendedJobInstance> jobInstances();
    protected abstract Map<Long, ExtendedJobExecution> jobExecutions();
    protected abstract Map<Long, ExtendedStepExecution> stepExecutions();
    protected abstract Map<Long, PartitionExecution> partitionExecutions();
    protected abstract Map<Long, CopyOnWriteArrayList<Long>> jobInstanceExecutions();
    protected abstract Map<Long, Long> jobExecutionInstances();
    protected abstract Map<Long, CopyOnWriteArraySet<Long>> jobExecutionStepExecutions();
    protected abstract Map<Long, Long> latestJobExecutionForInstance();
    protected abstract Map<Long, CopyOnWriteArrayList<Long>> stepExecutionPartitionExecutions();
    protected abstract Map<Long, CopyOnWriteArraySet<Long>> jobExecutionHistory();

    protected abstract long _id(final String key) throws Exception;

    protected abstract Set<String> fetchJobNames() throws Exception;
    protected abstract int fetchJobInstanceCount(final String jobName) throws Exception;
    protected abstract List<JobInstance> fetchJobInstances(final String jobName) throws Exception;
    protected abstract List<Long> fetchRunningJobExecutionIds(final String jobName) throws Exception;
    protected abstract List<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception;

    @Override
    public synchronized ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        final long id = _id(JOB_INSTANCE_ID);
        final ExtendedJobInstance instance = newJobInstanceBuilder()
                .setJobInstanceId(id)
                .setJobName(jobId)
                .setJslName(jslName)
                .setCreateTime(timestamp)
                .build();
        jobInstances().put(id, instance);
        jobInstanceExecutions().put(id, new CopyOnWriteArrayList<Long>());
        return instance;
    }

    @Override
    public synchronized ExtendedJobExecution createJobExecution(final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws Exception {
        final CopyOnWriteArrayList<Long> executions = jobInstanceExecutions().get(jobInstanceId);
        if (executions == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
        }
        final long jobExecutionId = _id(JOB_EXECUTION_ID);
        executions.add(jobExecutionId);
        jobInstanceExecutions().put(jobInstanceId, executions);
        final ExtendedJobExecution execution = newJobExecutionBuilder()
                .setJobInstanceId(jobInstanceId)
                .setJobExecutionId(jobExecutionId)
                .setJobName(jobName)
                .setBatchStatus(BatchStatus.STARTING)
                .setJobParameters(parameters)
                .setCreateTime(timestamp)
                .setLastUpdatedTime(timestamp)
                .build();
        jobExecutions().put(jobExecutionId, execution);
        jobExecutionInstances().put(jobExecutionId, jobInstanceId);
        latestJobExecutionForInstance().put(jobInstanceId, jobExecutionId);
        jobExecutionStepExecutions().put(jobExecutionId, new CopyOnWriteArraySet<Long>());
        jobExecutionHistory().put(jobExecutionId, new CopyOnWriteArraySet<Long>());
        return execution;
    }

    @Override
    public synchronized ExtendedStepExecution createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception {
        final CopyOnWriteArraySet<Long> executionIds = jobExecutionStepExecutions().get(jobExecutionId);
        if (executionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        final long stepExecutionId = _id(STEP_EXECUTION_ID);
        executionIds.add(stepExecutionId);
        jobExecutionStepExecutions().put(jobExecutionId, executionIds);
        final ExtendedStepExecution execution = newStepExecutionBuilder()
                .setJobExecutionId(jobExecutionId)
                .setStepExecutionId(stepExecutionId)
                .setStepName(stepName)
                .setCreateTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(BatchStatus.STARTING)
                .setMetrics(MutableMetricImpl.empty())
                .build();
        stepExecutions().put(stepExecutionId, execution);
        stepExecutionPartitionExecutions().put(stepExecutionId, new CopyOnWriteArrayList<Long>());
        return execution;
    }

    @Override
    public synchronized PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final CopyOnWriteArrayList<Long> partitions = stepExecutionPartitionExecutions().get(stepExecutionId);
        if (partitions == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        final long id = _id(PARTITION_EXECUTION_ID);
        partitions.add(id);
        stepExecutionPartitionExecutions().put(stepExecutionId, partitions);
        final PartitionExecution execution = newPartitionExecutionBuilder()
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
        partitionExecutions().put(id, execution);
        return execution;
    }

    @Override
    public synchronized void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        final ExtendedJobExecution execution = jobExecutions().get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutions().put(jobExecutionId, JobExecutionImpl.from(execution)
                .setLastUpdatedTime(timestamp)
                .setStartTime(timestamp)
                .setBatchStatus(BatchStatus.STARTED)
                .build()
        );
    }

    @Override
    public synchronized void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        final ExtendedJobExecution execution = jobExecutions().get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutions().put(jobExecutionId, JobExecutionImpl.from(execution)
                .setLastUpdatedTime(timestamp)
                .setBatchStatus(batchStatus)
                .build()
        );
    }

    @Override
    public synchronized void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        final ExtendedJobExecution execution = jobExecutions().get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutions().put(jobExecutionId, JobExecutionImpl.from(execution)
                .setBatchStatus(batchStatus)
                .setExitStatus(exitStatus)
                .setRestartElementId(restartElementId)
                .setLastUpdatedTime(timestamp)
                .setEndTime(timestamp)
                .build()
        );
    }

    @Override
    public synchronized void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        final CopyOnWriteArraySet<Long> oldJobExecutionIds = jobExecutionHistory().get(restartJobExecutionId);
        if (oldJobExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", restartJobExecutionId));
        }
        final CopyOnWriteArraySet<Long> jobExecutionIds = jobExecutionHistory().get(jobExecutionId);
        if (jobExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutionIds.add(restartJobExecutionId);
        jobExecutionIds.addAll(oldJobExecutionIds);
        jobExecutionHistory().put(jobExecutionId, jobExecutionIds);
    }

    @Override
    public synchronized void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        final ExtendedStepExecution execution = stepExecutions().get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions().put(stepExecutionId, StepExecutionImpl.from(execution)
                .setStartTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(BatchStatus.STARTED)
                .build()
        );
    }

    @Override
    public synchronized void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final ExtendedStepExecution execution = stepExecutions().get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions().put(stepExecutionId, StepExecutionImpl.from(execution)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public synchronized void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final ExtendedStepExecution execution = stepExecutions().get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions().put(stepExecutionId, StepExecutionImpl.from(execution)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .build()
        );
    }

    @Override
    public synchronized void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final ExtendedStepExecution execution = stepExecutions().get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions().put(stepExecutionId, StepExecutionImpl.from(execution)
                .setBatchStatus(batchStatus)
                .setExitStatus(exitStatus)
                .setUpdatedTime(timestamp)
                .setEndTime(timestamp)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public synchronized void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        final PartitionExecution partition = partitionExecutions().get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
        }
        partitionExecutions().put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                .setUpdatedTime(timestamp)
                .setStartTime(timestamp)
                .setBatchStatus(BatchStatus.STARTED)
                .build()
        );
    }

    @Override
    public synchronized void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final PartitionExecution partition = partitionExecutions().get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
        }
        partitionExecutions().put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                .setUpdatedTime(timestamp)
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public synchronized void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final PartitionExecution partition = partitionExecutions().get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
        }
        partitionExecutions().put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                .setEndTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(batchStatus)
                .setExitStatus(exitStatus)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        return fetchJobNames();
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        int count = fetchJobInstanceCount(jobName);
        if (count == 0) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
        }
        return count;
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        final List<JobInstance> ret = new ArrayList<>(count);
        final Collection<JobInstance> instances = fetchJobInstances(jobName);
        ret.addAll(instances);
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
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        final List<Long> ids = new ArrayList<>();
        final Collection<Long> values = fetchRunningJobExecutionIds(jobName);
        ids.addAll(values);
        if (ids.isEmpty()) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
        }
        return ids;
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        final JobExecution execution = jobExecutions().get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        return execution.getJobParameters();
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        final ExtendedJobInstance instance = jobInstances().get(jobInstanceId);
        if (instance == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
        }
        return instance;
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        final ExtendedJobExecution jobExecution = jobExecutions().get(jobExecutionId);
        if (jobExecution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        return getJobInstance(jobExecution.getJobInstanceId());
    }

    @Override
    public List<JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        final List<JobExecution> executions = new ArrayList<>();
        final Collection<JobExecution> values = fetchJobExecutionsForJobInstance(jobInstanceId);
        executions.addAll(values);
        if (executions.isEmpty()) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
        }
        return executions;
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        final ExtendedJobExecution execution = jobExecutions().get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        return execution;
    }

    @Override
    public synchronized ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        final ExtendedJobExecution jobExecution;
        jobExecution = jobExecutions().get(jobExecutionId);
        if (jobExecution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        final Long latest = latestJobExecutionForInstance().get(jobExecution.getJobInstanceId());
        if (latest == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobExecutionId));
        }
        if (latest != jobExecutionId) {
            throw new JobExecutionNotMostRecentException(Messages.format("CHAINLINK-006004.repository.not.most.recent.execution", jobExecutionId, jobExecution));
        }
        switch (jobExecution.getBatchStatus()) {
            case STOPPED:
            case FAILED:
                break;
            case COMPLETED:
                throw new JobExecutionAlreadyCompleteException(Messages.format("CHAINLINK-006006.repository.execution.already.complete", jobExecutionId));
            default:
                throw new JobRestartException(Messages.format("CHAINLINK-006007.repository.execution.not.eligible.for.restart", jobExecution.getExecutionId(), BatchStatus.STOPPED, BatchStatus.FAILED, jobExecution.getBatchStatus()));
        }
        return createJobExecution(
                jobExecution.getJobInstanceId(),
                jobExecution.getJobName(),
                parameters,
                new Date()
        );
    }

    @Override
    public List<StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        final CopyOnWriteArraySet<Long> stepExecutionIds = jobExecutionStepExecutions().get(jobExecutionId);
        if (stepExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        final List<StepExecution> stepExecutions = new ArrayList<>(stepExecutionIds.size());
        for (final Long stepExecutionId : stepExecutionIds) {
            stepExecutions.add(this.stepExecutions().get(stepExecutionId));
        }
        return stepExecutions;
    }

    @Override
    public synchronized ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        final CopyOnWriteArraySet<Long> historicJobExecutionIds = jobExecutionHistory().get(jobExecutionId);
        if (historicJobExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        CopyOnWriteArraySet<Long> executionIds = jobExecutionStepExecutions().get(jobExecutionId);
        if (executionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        stepExecutionIds.addAll(executionIds);
        for (final Long historicJobExecutionId : historicJobExecutionIds) {
            executionIds = jobExecutionStepExecutions().get(historicJobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", historicJobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
        }
        Date currentStepExecutionCreateTime = null;
        final List<ExtendedStepExecution> candidates = new ArrayList<>();
            for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
                final long id = it.next();
                final ExtendedStepExecution stepExecution = stepExecutions().get(id);
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
    public synchronized ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        final CopyOnWriteArraySet<Long> executions = jobExecutionHistory().get(jobExecutionId);
        if (executions == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        historicJobExecutionIds.addAll(executions);
        final TLongSet stepExecutionIds = new TLongHashSet();
        CopyOnWriteArraySet<Long> executionIds = jobExecutionStepExecutions().get(jobExecutionId);
        if (executionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        stepExecutionIds.addAll(executionIds);
        for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
            final long historicJobExecutionId = it.next();
            executionIds = jobExecutionStepExecutions().get(historicJobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", historicJobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<>();
        for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
            final ExtendedStepExecution execution = stepExecutions().get(it.next());
            if (stepName.equals(execution.getStepName())) {
                candidates.add(execution);
            }
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

    @Override
    public synchronized int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        final CopyOnWriteArraySet<Long> executions = jobExecutionHistory().get(jobExecutionId);
        if (executions == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
        }
        historicJobExecutionIds.addAll(executions);
        final TLongSet stepExecutionIds = new TLongHashSet();
        for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
            final long historicJobExecutionId = it.next();
            CopyOnWriteArraySet<Long> executionIds = jobExecutionStepExecutions().get(historicJobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", historicJobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<>(stepExecutionIds.size());
        for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
            final long stepExecutionId = it.next();
            final ExtendedStepExecution stepExecution = stepExecutions().get(stepExecutionId);
            if (stepExecution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            if (stepName.equals(stepExecution.getStepName())) {
                candidates.add(stepExecution);
            }
        }
        return candidates.size();
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        return stepExecutions().get(stepExecutionId);
    }

    @Override
    public synchronized StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        final StepExecution[] executions = new StepExecution[stepExecutionIds.length];
        for (int i = 0; i < stepExecutionIds.length; ++i) {
            executions[i] = stepExecutions().get(stepExecutionIds[i]);
        }
        return executions;
    }

    @Override
    public synchronized PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        final CopyOnWriteArrayList<Long> partitionIds = stepExecutionPartitionExecutions().get(stepExecutionId);
        if (partitionIds.isEmpty()) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
        }
        final List<PartitionExecution> ret = new ArrayList<>();
        for (final Long partitionExecutionId : partitionIds) {
            final PartitionExecution partitionExecution = partitionExecutions().get(partitionExecutionId);
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
    }

    @Override
    public synchronized PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        final PartitionExecution partition = partitionExecutions().get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
        }
        return partition;
    }

    protected ExtendedJobInstanceBuilder<?> newJobInstanceBuilder() {
        return new JobInstanceImpl.Builder();
    }

    protected ExtendedJobExecutionBuilder<?> newJobExecutionBuilder() {
        return new JobExecutionImpl.Builder();
    }

    protected ExtendedStepExecutionBuilder<?> newStepExecutionBuilder() {
        return new StepExecutionImpl.Builder();
    }

    protected PartitionExecutionBuilder<?> newPartitionExecutionBuilder() {
        return new PartitionExecutionImpl.Builder();
    }
}
