package io.machinecode.chainlink.repository.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
import com.tangosol.util.Filter;
import com.tangosol.util.InvocableMap;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.THashSet;
import gnu.trove.set.hash.TLongHashSet;
import io.machinecode.chainlink.repository.core.JobExecutionImpl;
import io.machinecode.chainlink.repository.core.JobInstanceImpl;
import io.machinecode.chainlink.repository.core.MutableMetricImpl;
import io.machinecode.chainlink.repository.core.PartitionExecutionImpl;
import io.machinecode.chainlink.repository.core.StepExecutionImpl;
import io.machinecode.chainlink.spi.marshalling.Marshaller;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.util.Messages;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceExecutonRepository implements ExecutionRepository {

    protected static final String IDS = CoherenceExecutonRepository.class.getCanonicalName() + ".ids";
    protected static final String JOB_INSTANCES = CoherenceExecutonRepository.class.getCanonicalName() + ".jobInstances";
    protected static final String JOB_EXECUTIONS = CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutions";
    protected static final String STEP_EXECUTIONS = CoherenceExecutonRepository.class.getCanonicalName() + ".stepExecutions";
    protected static final String PARTITION_EXECUTIONS = CoherenceExecutonRepository.class.getCanonicalName() + ".partitionExecutions";
    protected static final String JOB_INSTANCE_EXECUTIONS = CoherenceExecutonRepository.class.getCanonicalName() + ".jobInstanceExecutions";
    protected static final String JOB_EXECUTION_INSTANCES = CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutionInstances";
    protected static final String JOB_EXECUTION_STEP_EXECUTIONS = CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutionStepExecutions";
    protected static final String LATEST_JOB_EXECUTION_FOR_INSTANCE = CoherenceExecutonRepository.class.getCanonicalName() + ".latestJobExecutionForInstance";
    protected static final String STEP_EXECUTION_PARTITION_EXECUTIONS = CoherenceExecutonRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions";
    protected static final String JOB_EXECUTION_HISTORY = CoherenceExecutonRepository.class.getCanonicalName() + ".jobExecutionHistory";

    protected static final String JOB_INSTANCE_ID = "job_instance_id";
    protected static final String JOB_EXECUTION_ID = "job_execution_id";
    protected static final String STEP_EXECUTION_ID = "step_execution_id";
    protected static final String PARTITION_EXECUTION_ID = "partition_execution_id";
    public static final Filter ALL_ENTRIES = new Filter() {
        @Override
        public boolean evaluate(final Object o) {
            return true;
        }
    };

    protected final NamedCache ids;
    protected final NamedCache jobInstances;
    protected final NamedCache jobExecutions;
    protected final NamedCache stepExecutions;
    protected final NamedCache partitionExecutions;
    protected final NamedCache jobInstanceExecutions;
    protected final NamedCache jobExecutionInstances;
    protected final NamedCache jobExecutionStepExecutions;
    protected final NamedCache latestJobExecutionForInstance;
    protected final NamedCache stepExecutionPartitionExecutions;
    protected final NamedCache jobExecutionHistory;

    protected final Marshaller marshaller;

    public CoherenceExecutonRepository(final Marshaller marshaller) {
        this.marshaller = marshaller;

        this.ids = CacheFactory.getCache(IDS);
        this.jobInstances = CacheFactory.getCache(JOB_INSTANCES);
        this.jobExecutions = CacheFactory.getCache(JOB_EXECUTIONS);
        this.stepExecutions = CacheFactory.getCache(STEP_EXECUTIONS);
        this.partitionExecutions = CacheFactory.getCache(PARTITION_EXECUTIONS);
        this.jobInstanceExecutions = CacheFactory.getCache(JOB_INSTANCE_EXECUTIONS);
        this.jobExecutionInstances = CacheFactory.getCache(JOB_EXECUTION_INSTANCES);
        this.jobExecutionStepExecutions = CacheFactory.getCache(JOB_EXECUTION_STEP_EXECUTIONS);
        this.latestJobExecutionForInstance = CacheFactory.getCache(LATEST_JOB_EXECUTION_FOR_INSTANCE);
        this.stepExecutionPartitionExecutions = CacheFactory.getCache(STEP_EXECUTION_PARTITION_EXECUTIONS);
        this.jobExecutionHistory = CacheFactory.getCache(JOB_EXECUTION_HISTORY);
    }

    private long _id(final String key) throws Exception {
        try {
            final long id;
            ids.lock(key);
            final Long that = (Long)ids.get(key);
            id = that == null ? 1 : that + 1;
            ids.put(key, id);
            return id;
        } finally {
            this.ids.unlock(key);
        }
    }

    @Override
    public JobInstanceImpl createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        final long id = _id(JOB_INSTANCE_ID);
        final JobInstanceImpl instance = new JobInstanceImpl.Builder()
                .setInstanceId(id)
                .setJobName(jobId)
                .setJslName(jslName)
                .setCreatedTime(timestamp)
                .build();
        jobInstances.put(id, instance);
        jobInstanceExecutions.put(id, new CopyOnWriteArrayList<Long>());
        return instance;
    }

    @Override
    public JobExecutionImpl createJobExecution(final ExtendedJobInstance instance, final Properties parameters, final Date timestamp) throws Exception {
        final CopyOnWriteArrayList<Long> executions = (CopyOnWriteArrayList<Long>)jobInstanceExecutions.get(instance.getInstanceId());
        if (executions == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", instance.getInstanceId()));
        }
        final long jobExecutionId = _id(JOB_EXECUTION_ID);
        executions.add(jobExecutionId);
        jobInstanceExecutions.put(instance.getInstanceId(), executions);
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setJobInstanceId(instance.getInstanceId())
                .setJobExecutionId(jobExecutionId)
                .setJobName(instance.getJobName())
                .setBatchStatus(BatchStatus.STARTING)
                .setParameters(parameters)
                .setCreatedTime(timestamp)
                .setUpdatedTime(timestamp)
                .build();
        jobExecutions.put(jobExecutionId, execution);
        jobExecutionInstances.put(jobExecutionId, instance.getInstanceId());
        latestJobExecutionForInstance.put(instance.getInstanceId(), jobExecutionId);
        jobExecutionStepExecutions.put(jobExecutionId, new CopyOnWriteArraySet<Long>());
        jobExecutionHistory.put(jobExecutionId, new CopyOnWriteArraySet<Long>());
        return execution;
    }

    @Override
    public StepExecutionImpl createStepExecution(final ExtendedJobExecution jobExecution, final String stepName, final Date timestamp) throws Exception {
        final CopyOnWriteArraySet<Long> executionIds = (CopyOnWriteArraySet<Long>)jobExecutionStepExecutions.get(jobExecution.getExecutionId());
        if (executionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecution.getExecutionId()));
        }
        final long stepExecutionId = _id(STEP_EXECUTION_ID);
        executionIds.add(stepExecutionId);
        jobExecutionStepExecutions.put(jobExecution.getExecutionId(), executionIds);
        final StepExecutionImpl execution = new StepExecutionImpl.Builder()
                .setJobExecutionId(jobExecution.getExecutionId())
                .setStepExecutionId(stepExecutionId)
                .setStepName(stepName)
                .setCreatedTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(BatchStatus.STARTING)
                .setMetrics(MutableMetricImpl.empty())
                .build();
        stepExecutions.putIfAbsent(stepExecutionId, execution);
        stepExecutionPartitionExecutions.put(stepExecutionId, new CopyOnWriteArrayList<Long>());
        return execution;
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshaller.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshaller.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshaller.clone(writerCheckpoint);
        final CopyOnWriteArrayList<Long> partitions = (CopyOnWriteArrayList<Long>)stepExecutionPartitionExecutions.get(stepExecutionId);
        if (partitions == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        final long id = _id(PARTITION_EXECUTION_ID);
        partitions.add(id);
        stepExecutionPartitionExecutions.put(stepExecutionId, partitions);
        final PartitionExecutionImpl execution = new PartitionExecutionImpl.Builder()
                .setPartitionExecutionId(id)
                .setStepExecutionId(stepExecutionId)
                .setPartitionId(partitionId)
                .setPartitionProperties(properties)
                .setCreatedTime(timestamp)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .setMetrics(MutableMetricImpl.empty())
                .setBatchStatus(BatchStatus.STARTING)
                .build();
        partitionExecutions.put(id, execution);
        return execution;
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        final ExtendedJobExecution execution = (ExtendedJobExecution)jobExecutions.get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                .setUpdatedTime(timestamp)
                .setStartTime(timestamp)
                .setBatchStatus(BatchStatus.STARTED)
                .build()
        );
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        final ExtendedJobExecution execution = (ExtendedJobExecution)jobExecutions.get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                .setUpdatedTime(timestamp)
                .setBatchStatus(batchStatus)
                .build()
        );
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        final ExtendedJobExecution execution = (ExtendedJobExecution)jobExecutions.get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutions.put(jobExecutionId, JobExecutionImpl.from(execution)
                .setBatchStatus(batchStatus)
                .setExitStatus(exitStatus)
                .setRestartElementId(restartElementId)
                .setUpdatedTime(timestamp)
                .setEndTime(timestamp)
                .build()
        );
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        final CopyOnWriteArraySet<Long> oldJobExecutionIds = (CopyOnWriteArraySet<Long>)jobExecutionHistory.get(restartJobExecutionId);
        if (oldJobExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", restartJobExecutionId));
        }
        final CopyOnWriteArraySet<Long> jobExecutionIds = (CopyOnWriteArraySet<Long>)jobExecutionHistory.get(jobExecutionId);
        if (jobExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        jobExecutionIds.add(restartJobExecutionId);
        jobExecutionIds.addAll(oldJobExecutionIds);
        jobExecutionHistory.put(jobExecutionId, jobExecutionIds);
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        final ExtendedStepExecution execution = (ExtendedStepExecution)stepExecutions.get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                .setStartTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(BatchStatus.STARTED)
                .build()
        );
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshaller.clone(persistentUserData);
        final ExtendedStepExecution execution = (ExtendedStepExecution)stepExecutions.get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshaller.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshaller.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshaller.clone(writerCheckpoint);
        final ExtendedStepExecution execution = (ExtendedStepExecution)stepExecutions.get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .build()
        );
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final ExtendedStepExecution execution = (ExtendedStepExecution)stepExecutions.get(stepExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                .setBatchStatus(batchStatus)
                .setExitStatus(exitStatus)
                .setUpdatedTime(timestamp)
                .setEndTime(timestamp)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        final PartitionExecution partition = (PartitionExecution)partitionExecutions.get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
        }
        partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                .setUpdatedTime(timestamp)
                .setStartTime(timestamp)
                .setBatchStatus(BatchStatus.STARTED)
                .build()
        );
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshaller.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshaller.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshaller.clone(writerCheckpoint);
        final PartitionExecution partition = (PartitionExecution)partitionExecutions.get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
        }
        partitionExecutions.put(partitionExecutionId, PartitionExecutionImpl.from(partition)
                .setUpdatedTime(timestamp)
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .setPersistentUserData(clonedPersistentUserData)
                .setMetrics(MutableMetricImpl.copy(metrics))
                .build()
        );
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshaller.clone(persistentUserData);
        final PartitionExecution partition = (PartitionExecution)partitionExecutions.get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
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
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        final Map results = jobInstances.invokeAll(ALL_ENTRIES, new JobNameProcessor());
        final Set<String> ret = new THashSet<String>();
        for (final Object value : results.entrySet()) {
            ret.add((String) value);
        }
        return ret;
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        final Map results = jobInstances.invokeAll(ALL_ENTRIES, new JobInstanceCountProcessor(jobName));
        int count = 0;
        for (final Object value : results.values()) {
            if ((Boolean)value) {
                ++count;
            }
        }
        if (count == 0) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
        }
        return count;
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        final Map results = jobInstances.invokeAll(ALL_ENTRIES, new JobInstanceProcessor(jobName));
        final List<JobInstance> ret = new ArrayList<JobInstance>(count);
        for (final Object value : results.values()) {
            if (value != null) {
                ret.add((JobInstance)value);
            }
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
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        final Map results = jobExecutions.invokeAll(ALL_ENTRIES, new RunningJobExecutionIdProcessor(jobName));
        final List<Long> ids = new ArrayList<Long>();
        for (final Object value : results.values()) {
            if (value != null) {
                ids.add((Long) value);
            }
        }
        if (ids.isEmpty()) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
        }
        return ids;
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        final JobExecution execution = (JobExecution) jobExecutions.get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        return execution.getJobParameters();
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        final ExtendedJobInstance instance = (ExtendedJobInstance) jobInstances.get(jobInstanceId);
        if (instance == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
        }
        return instance;
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        final ExtendedJobExecution jobExecution = (ExtendedJobExecution) jobExecutions.get(jobExecutionId);
        if (jobExecution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        return getJobInstance(jobExecution.getJobInstanceId());
    }

    @Override
    public List<JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        final Map results = jobExecutions.invokeAll(ALL_ENTRIES, new JobExecutionsForJobInstanceProcessor(jobInstanceId));
        final List<JobExecution> executions = new ArrayList<JobExecution>();
        for (final Object result : results.values()) {
            if (result != null) {
                executions.add((JobExecution) result);
            }
        }
        if (executions.isEmpty()) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
        }
        return executions;
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        final ExtendedJobExecution execution = (ExtendedJobExecution) jobExecutions.get(jobExecutionId);
        if (execution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        return execution;
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        final ExtendedJobExecution jobExecution = (ExtendedJobExecution) jobExecutions.get(jobExecutionId);
        if (jobExecution == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        final Long latest = (Long) latestJobExecutionForInstance.get(jobExecution.getJobInstanceId());
        if (latest == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobExecutionId));
        }
        if (latest != jobExecutionId) {
            throw new JobExecutionNotMostRecentException(Messages.format("CHAINLINK-006004.repository.not.most.recent.execution", jobExecutionId, jobExecution));
        }
        switch (jobExecution.getBatchStatus()) {
            case STOPPED:
            case FAILED:
                break;
            case COMPLETED:
                throw new JobExecutionAlreadyCompleteException(Messages.format("CHAINLINK-006006.execution.repository.execution.already.complete", jobExecutionId));
            default:
                throw new JobRestartException(Messages.format("CHAINLINK-006007.execution.repository.execution.not.eligible.for.restart", jobExecution.getExecutionId(), BatchStatus.STOPPED, BatchStatus.FAILED, jobExecution.getBatchStatus()));
        }
        return createJobExecution(
                getJobInstance(jobExecution.getJobInstanceId()),
                parameters,
                new Date()
        );
    }

    @Override
    public List<StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        final CopyOnWriteArraySet<Long> stepExecutionIds = (CopyOnWriteArraySet<Long>) jobExecutionStepExecutions.get(jobExecutionId);
        if (stepExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        final List<StepExecution> stepExecutions = new ArrayList<StepExecution>(stepExecutionIds.size());
        for (final Long stepExecutionId : stepExecutionIds) {
            stepExecutions.add((StepExecution) this.stepExecutions.get(stepExecutionId));
        }
        return stepExecutions;
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        final CopyOnWriteArraySet<Long> historicJobExecutionIds = (CopyOnWriteArraySet<Long>) jobExecutionHistory.get(jobExecutionId);
        if (historicJobExecutionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        final TLongSet stepExecutionIds = new TLongHashSet();
        CopyOnWriteArraySet<Long> executionIds = (CopyOnWriteArraySet<Long>) jobExecutionStepExecutions.get(jobExecutionId);
        if (executionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        stepExecutionIds.addAll(executionIds);
        for (final Long historicJobExecutionId : historicJobExecutionIds) {
            executionIds = (CopyOnWriteArraySet<Long>) jobExecutionStepExecutions.get(historicJobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", historicJobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
        }
        Date currentStepExecutionCreateTime = null;
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>();
        for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
            final long id = it.next();
            final ExtendedStepExecution stepExecution = (ExtendedStepExecution) stepExecutions.get(id);
            if (stepExecutionId == id) {
                if (stepExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
                }
                currentStepExecutionCreateTime = stepExecution.getCreateTime();
                continue;
            }
            if (stepName.equals(stepExecution.getStepName())) {
                candidates.add(stepExecution);
            }
        }
        if (currentStepExecutionCreateTime == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
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
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        final CopyOnWriteArraySet<Long> executions = (CopyOnWriteArraySet<Long>) jobExecutionHistory.get(jobExecutionId);
        if (executions == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        historicJobExecutionIds.addAll(executions);
        final TLongSet stepExecutionIds = new TLongHashSet();
        CopyOnWriteArraySet<Long> executionIds = (CopyOnWriteArraySet<Long>) jobExecutionStepExecutions.get(jobExecutionId);
        if (executionIds == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        stepExecutionIds.addAll(executionIds);
        for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
            final long historicJobExecutionId = it.next();
            executionIds = (CopyOnWriteArraySet<Long>) jobExecutionStepExecutions.get(historicJobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", historicJobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>();
        for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
            final ExtendedStepExecution execution = (ExtendedStepExecution) stepExecutions.get(it.next());
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
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        final TLongSet historicJobExecutionIds = new TLongHashSet();
        final CopyOnWriteArraySet<Long> executions = (CopyOnWriteArraySet<Long>) jobExecutionHistory.get(jobExecutionId);
        if (executions == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
        }
        historicJobExecutionIds.addAll(executions);
        final TLongSet stepExecutionIds = new TLongHashSet();
        for (final TLongIterator it = historicJobExecutionIds.iterator(); it.hasNext();) {
            final long historicJobExecutionId = it.next();
            CopyOnWriteArraySet<Long> executionIds = (CopyOnWriteArraySet<Long>) jobExecutionStepExecutions.get(historicJobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", historicJobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
        }
        final List<ExtendedStepExecution> candidates = new ArrayList<ExtendedStepExecution>(stepExecutionIds.size());
        for (final TLongIterator it = stepExecutionIds.iterator(); it.hasNext();) {
            final long stepExecutionId = it.next();
            final ExtendedStepExecution stepExecution = (ExtendedStepExecution) stepExecutions.get(stepExecutionId);
            if (stepExecution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            if (stepName.equals(stepExecution.getStepName())) {
                candidates.add(stepExecution);
            }
        }
        return candidates.size();
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        return (ExtendedStepExecution) stepExecutions.get(stepExecutionId);
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        final StepExecution[] executions = new StepExecution[stepExecutionIds.length];
        for (int i = 0; i < stepExecutionIds.length; ++i) {
            executions[i] = (StepExecution) stepExecutions.get(stepExecutionIds[i]);
        }
        return executions;
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        final CopyOnWriteArrayList<Long> partitionIds = (CopyOnWriteArrayList<Long>) stepExecutionPartitionExecutions.get(stepExecutionId);
        if (partitionIds.isEmpty()) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
        }
        final List<PartitionExecution> ret = new ArrayList<PartitionExecution>();
        for (final Long partitionExecutionId : partitionIds) {
            final PartitionExecution partitionExecution = (PartitionExecution) partitionExecutions.get(partitionExecutionId);
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
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        final PartitionExecution partition = (PartitionExecution) partitionExecutions.get(partitionExecutionId);
        if (partition == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
        }
        return partition;
    }

    @Override
    public boolean isLocal() {
        return true;
    }
}
