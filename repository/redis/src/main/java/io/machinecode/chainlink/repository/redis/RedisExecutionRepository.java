package io.machinecode.chainlink.repository.redis;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.repository.JobExecutionImpl;
import io.machinecode.chainlink.core.repository.JobInstanceImpl;
import io.machinecode.chainlink.core.repository.MutableMetricImpl;
import io.machinecode.chainlink.core.repository.PartitionExecutionImpl;
import io.machinecode.chainlink.core.repository.StepExecutionImpl;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.util.Messages;
import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;

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
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RedisExecutionRepository implements ExecutionRepository {

    protected final byte[] JOB_INSTANCE_ID;
    protected final byte[] JOB_EXECUTION_ID;
    protected final byte[] STEP_EXECUTION_ID;
    protected final byte[] PARTITION_EXECUTION_ID;

    protected final byte[] JOB_INSTANCE_PREFIX;
    protected final byte[] JOB_EXECUTION_PREFIX;
    protected final byte[] STEP_EXECUTION_PREFIX;
    protected final byte[] PARTITION_EXECUTION_PREFIX;

    protected final byte[] JOB_EXECUTION_HISTORY_PREFIX;
    protected final byte[] STEP_EXECUTION_PARTITION_EXECUTIONS_PREFIX;
    protected final byte[] JOB_INSTANCE_EXECUTIONS_PREFIX;
    protected final byte[] JOB_NAME_JOB_INSTANCES_PREFIX;
    protected final byte[] JOB_NAME_JOB_EXECUTIONS_PREFIX;
    protected final byte[] LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX;
    protected final byte[] JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX;

    protected final byte[] JOB_NAMES;

    protected final WeakReference<ClassLoader> loader;
    protected final JedisShardInfo info;
    protected final Marshalling marshalling;

    protected final Jedis _open() {
        return info.createResource();
    }

    public RedisExecutionRepository(final JedisShardInfo info, final ClassLoader loader, final Marshalling marshalling) throws Exception {
        this.loader = new WeakReference<>(loader);
        this.info = info;
        this.marshalling = marshalling;

        JOB_INSTANCE_ID = this.marshalling.marshall("ji_id");
        JOB_EXECUTION_ID = this.marshalling.marshall("je_id");
        STEP_EXECUTION_ID = this.marshalling.marshall("se_id");
        PARTITION_EXECUTION_ID = this.marshalling.marshall("pe_id");

        JOB_INSTANCE_PREFIX = this.marshalling.marshall("ji_");
        JOB_EXECUTION_PREFIX = this.marshalling.marshall("je_");
        STEP_EXECUTION_PREFIX = this.marshalling.marshall("se_");
        PARTITION_EXECUTION_PREFIX = this.marshalling.marshall("pe_");

        JOB_EXECUTION_HISTORY_PREFIX = this.marshalling.marshall("jeh_");
        STEP_EXECUTION_PARTITION_EXECUTIONS_PREFIX = this.marshalling.marshall("sepe_");
        JOB_INSTANCE_EXECUTIONS_PREFIX = this.marshalling.marshall("jie_");
        JOB_NAME_JOB_INSTANCES_PREFIX = this.marshalling.marshall("jnji_");
        JOB_NAME_JOB_EXECUTIONS_PREFIX = this.marshalling.marshall("jnje_");
        LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX = this.marshalling.marshall("lje_");
        JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX = this.marshalling.marshall("jese_");

        JOB_NAMES = this.marshalling.marshall("jn");
    }

    @Override
    public JobInstanceImpl createJobInstance(final String jobId, final String jslName, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final long id = jedis.incr(JOB_INSTANCE_ID);
            final JobInstanceImpl instance = new JobInstanceImpl.Builder()
                    .setJobInstanceId(id)
                    .setJobName(jobId)
                    .setJslName(jslName)
                    .setCreateTime(timestamp)
                    .build();
            jedis.set(marshalling.marshall(JOB_INSTANCE_PREFIX, id), marshalling.marshall(instance));
            jedis.sadd(JOB_NAMES, marshalling.marshall(jobId));
            jedis.rpush(marshalling.marshall(JOB_NAME_JOB_INSTANCES_PREFIX, jobId), marshalling.marshall(id));
            return instance;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public JobExecutionImpl createJobExecution(final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            return _createJobExecution(jedis, jobInstanceId, jobName, parameters, timestamp);
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    private JobExecutionImpl _createJobExecution(final BinaryJedisCommands jedis, final long jobInstanceId, final String jobName, final Properties parameters, final Date timestamp) throws Exception {
        final long jobExecutionId = jedis.incr(JOB_EXECUTION_ID);
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setJobInstanceId(jobInstanceId)
                .setJobExecutionId(jobExecutionId)
                .setJobName(jobName)
                .setBatchStatus(BatchStatus.STARTING)
                .setJobParameters(parameters)
                .setCreateTime(timestamp)
                .setLastUpdatedTime(timestamp)
                .build();
        jedis.set(marshalling.marshall(JOB_EXECUTION_PREFIX, jobExecutionId), marshalling.marshall(execution));
        jedis.set(
                marshalling.marshall(LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX, jobInstanceId),
                marshalling.marshall(jobExecutionId)
        );
        jedis.rpush(
                marshalling.marshall(JOB_INSTANCE_EXECUTIONS_PREFIX, jobInstanceId),
                marshalling.marshall(jobExecutionId)
        );
        jedis.rpush(
                marshalling.marshall(JOB_NAME_JOB_EXECUTIONS_PREFIX, jobInstanceId),
                marshalling.marshall(jobExecutionId)
        );
        return execution;
    }

    @Override
    public StepExecutionImpl createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final long stepExecutionId = jedis.incr(STEP_EXECUTION_ID);
            final StepExecutionImpl execution = new StepExecutionImpl.Builder()
                    .setJobExecutionId(jobExecutionId)
                    .setStepExecutionId(stepExecutionId)
                    .setStepName(stepName)
                    .setCreateTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setBatchStatus(BatchStatus.STARTING)
                    .setMetrics(MutableMetricImpl.empty())
                    .build();
            jedis.set(
                    marshalling.marshall(STEP_EXECUTION_PREFIX, stepExecutionId),
                    marshalling.marshall(execution));
            jedis.rpush(
                    marshalling.marshall(JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId),
                    marshalling.marshall(stepExecutionId)
            );
            return execution;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        Jedis jedis = null;
        try {
            jedis = _open();
            final long id = jedis.incr(PARTITION_EXECUTION_ID);
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
            jedis.set(marshalling.marshall(PARTITION_EXECUTION_PREFIX, id), marshalling.marshall(execution));
            jedis.rpush(marshalling.marshall(STEP_EXECUTION_PARTITION_EXECUTIONS_PREFIX, stepExecutionId), marshalling.marshall(id));
            return execution;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution execution = _je(jedis, jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jedis.set(
                    marshalling.marshall(JOB_EXECUTION_PREFIX, jobExecutionId),
                    marshalling.marshall(JobExecutionImpl.from(execution)
                            .setLastUpdatedTime(timestamp)
                            .setStartTime(timestamp)
                            .setBatchStatus(BatchStatus.STARTED)
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution execution = _je(jedis, jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jedis.set(
                    marshalling.marshall(JOB_EXECUTION_PREFIX, jobExecutionId),
                    marshalling.marshall(JobExecutionImpl.from(execution)
                            .setLastUpdatedTime(timestamp)
                            .setBatchStatus(batchStatus)
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution execution = _je(jedis, jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            jedis.set(
                    marshalling.marshall(JOB_EXECUTION_PREFIX, jobExecutionId),
                    marshalling.marshall(JobExecutionImpl.from(execution)
                            .setBatchStatus(batchStatus)
                            .setExitStatus(exitStatus)
                            .setRestartElementId(restartElementId)
                            .setLastUpdatedTime(timestamp)
                            .setEndTime(timestamp)
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            List<byte[]> oldJobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, restartJobExecutionId);
            if (oldJobExecutionIds == null) {
                oldJobExecutionIds = Collections.emptyList();
            }
            List<byte[]> jobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId);
            if (jobExecutionIds == null) {
                jobExecutionIds = new ArrayList<>();
            }
            jobExecutionIds.add(marshalling.marshall(restartJobExecutionId));
            jobExecutionIds.addAll(oldJobExecutionIds);
            jedis.rpush(
                    marshalling.marshall(JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId),
                    jobExecutionIds.toArray(new byte[jobExecutionIds.size()][])
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedStepExecution execution = _se(jedis, stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    marshalling.marshall(STEP_EXECUTION_PREFIX, stepExecutionId),
                    marshalling.marshall(StepExecutionImpl.from(execution)
                            .setStartTime(timestamp)
                            .setUpdatedTime(timestamp)
                            .setBatchStatus(BatchStatus.STARTED)
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
            final ExtendedStepExecution execution = _se(jedis, stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    marshalling.marshall(STEP_EXECUTION_PREFIX, stepExecutionId),
                    marshalling.marshall(StepExecutionImpl.from(execution)
                            .setUpdatedTime(timestamp)
                            .setPersistentUserData(clonedPersistentUserData)
                            .setMetrics(MutableMetricImpl.copy(metrics))
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedStepExecution execution = _se(jedis, stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    marshalling.marshall(STEP_EXECUTION_PREFIX, stepExecutionId),
                    marshalling.marshall(StepExecutionImpl.from(execution)
                            .setUpdatedTime(timestamp)
                            .setPersistentUserData(clonedPersistentUserData)
                            .setMetrics(MutableMetricImpl.copy(metrics))
                            .setReaderCheckpoint(clonedReaderCheckpoint)
                            .setWriterCheckpoint(clonedWriterCheckpoint)
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedStepExecution execution = _se(jedis, stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    marshalling.marshall(STEP_EXECUTION_PREFIX, stepExecutionId),
                    marshalling.marshall(StepExecutionImpl.from(execution)
                            .setBatchStatus(batchStatus)
                            .setExitStatus(exitStatus)
                            .setUpdatedTime(timestamp)
                            .setEndTime(timestamp)
                            .setMetrics(MutableMetricImpl.copy(metrics))
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final PartitionExecution partition = _pe(jedis, partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            jedis.set(
                    marshalling.marshall(PARTITION_EXECUTION_PREFIX, partitionExecutionId),
                    marshalling.marshall(PartitionExecutionImpl.from(partition)
                            .setUpdatedTime(timestamp)
                            .setStartTime(timestamp)
                            .setBatchStatus(BatchStatus.STARTED)
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        Jedis jedis = null;
        try {
            jedis = _open();
            final PartitionExecution partition = _pe(jedis, partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            jedis.set(
                    marshalling.marshall(PARTITION_EXECUTION_PREFIX, partitionExecutionId),
                    marshalling.marshall(PartitionExecutionImpl.from(partition)
                            .setUpdatedTime(timestamp)
                            .setReaderCheckpoint(clonedReaderCheckpoint)
                            .setWriterCheckpoint(clonedWriterCheckpoint)
                            .setPersistentUserData(clonedPersistentUserData)
                            .setMetrics(MutableMetricImpl.copy(metrics))
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws Exception {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        Jedis jedis = null;
        try {
            jedis = _open();
            final PartitionExecution partition = _pe(jedis, partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            jedis.set(
                    marshalling.marshall(PARTITION_EXECUTION_PREFIX, partitionExecutionId),
                    marshalling.marshall(PartitionExecutionImpl.from(partition)
                            .setEndTime(timestamp)
                            .setUpdatedTime(timestamp)
                            .setBatchStatus(batchStatus)
                            .setExitStatus(exitStatus)
                            .setPersistentUserData(clonedPersistentUserData)
                            .setMetrics(MutableMetricImpl.copy(metrics))
                            .build())
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public Set<String> getJobNames() throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final Set<byte[]> names = jedis.smembers(JOB_NAMES);
            final Set<String> ret = new THashSet<>();
            for (final byte[] name : names) {
                ret.add((String) marshalling.unmarshall(name, this.loader.get()));
            }
            return ret;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            long count = jedis.llen(marshalling.marshall(JOB_NAME_JOB_INSTANCES_PREFIX, jobName));
            if (count == 0) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            return (int)count;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws Exception {
        final List<JobInstance> ret = new ArrayList<>(count);
        Jedis jedis = null;
        final int end = start + count - 1;
        final int finish = end < 0 ? 0 : end;
        try {
            jedis = _open();
            final byte[] key = marshalling.marshall(JOB_NAME_JOB_INSTANCES_PREFIX, jobName);
            List<byte[]> ids = jedis.lrange(key, start, finish);
            if (ids.isEmpty() && jedis.llen(key) == 0) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            for (final byte[] id : ids) {
                ret.add(_ji(jedis, id));
            }
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
        return ret;
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final List<Long> ids = new ArrayList<>();
            final List<byte[]> values = _list(jedis, JOB_NAME_JOB_EXECUTIONS_PREFIX, jobName);
            for (final byte[] value : values) {
                final ExtendedJobExecution jobExecution = _je(jedis, value);
                if (jobName.equals(jobExecution.getJobName())) {
                    switch (jobExecution.getBatchStatus()) {
                        case STARTING:
                        case STARTED:
                            ids.add(jobExecution.getExecutionId());
                    }
                }
                ids.add(jobExecution.getExecutionId());
            }
            if (ids.isEmpty()) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            return ids;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution execution = _je(jedis, jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return execution.getJobParameters();
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            return _getJobInstance(jedis, jobInstanceId);
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }
    private ExtendedJobInstance _getJobInstance(final BinaryJedisCommands jedis, final long jobInstanceId) throws Exception {
        final ExtendedJobInstance instance = _ji(jedis, jobInstanceId);
        if (instance == null) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
        }
        return instance;
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution jobExecution = _je(jedis, jobExecutionId);
            if (jobExecution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return _getJobInstance(jedis, jobExecution.getJobInstanceId());
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        final List<JobExecution> executions = new ArrayList<>();
        Jedis jedis = null;
        try {
            jedis = _open();
            final List<byte[]> values = _list(jedis, JOB_INSTANCE_EXECUTIONS_PREFIX, jobInstanceId);
            for (final byte[] value : values) {
                executions.add(_je(jedis, value));
            }
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
        if (executions.isEmpty()) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
        }
        return executions;
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution execution = _je(jedis, jobExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return execution;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final ExtendedJobExecution jobExecution = _je(jedis, jobExecutionId);
            if (jobExecution == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final Long latest = (Long) marshalling.unmarshall(jedis.get(marshalling.marshall(LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX, jobExecution.getJobInstanceId())), this.loader.get());
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
            return _createJobExecution(
                    jedis,
                    jobExecution.getJobInstanceId(),
                    jobExecution.getJobName(),
                    parameters,
                    new Date()
            );
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public List<StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final List<byte[]> values = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId);
            if (values == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final List<StepExecution> stepExecutions = new ArrayList<>(values.size());
            for (final byte[] value : values) {
                stepExecutions.add(_se(jedis, value));
            }
            return stepExecutions;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        Jedis jedis = null;
        Date currentStepExecutionCreateTime = null;
        final List<ExtendedStepExecution> candidates = new ArrayList<>();
        try {
            jedis = _open();
            List<byte[]> historicJobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId);
            if (historicJobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final byte[] stepExecutionIdBytes = marshalling.marshall(stepExecutionId);
            final List<byte[]> stepExecutionIds = new ArrayList<>();

            List<byte[]> executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final byte[] historicJobExecutionId : historicJobExecutionIds) {
                executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", Arrays.toString(historicJobExecutionId)));
                }
                stepExecutionIds.addAll(executionIds);
            }
            for (final byte[] id : stepExecutionIds) {
                final ExtendedStepExecution stepExecution = _se(jedis, id);
                if (Arrays.equals(stepExecutionIdBytes, id)) {
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
        } finally {
            if(jedis != null) {
                jedis.disconnect();
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
        Jedis jedis = null;
        try {
            jedis = _open();
            List<byte[]> historicJobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId);
            if (historicJobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final List<byte[]> stepExecutionIds = new ArrayList<>();
            List<byte[]> executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final byte[] historicJobExecutionId : historicJobExecutionIds) {
                executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", Arrays.toString(historicJobExecutionId)));
                }
                stepExecutionIds.addAll(executionIds);
            }
            final List<ExtendedStepExecution> candidates = new ArrayList<>();
            for (final byte[] stepExecutionId : stepExecutionIds) {
                final ExtendedStepExecution execution = _se(jedis, stepExecutionId);
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
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            List<byte[]> historicJobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId);
            if (historicJobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final List<byte[]> stepExecutionIds = new ArrayList<>();
            for (final byte[] historicJobExecutionId : historicJobExecutionIds) {
                final List<byte[]> executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", Arrays.toString(historicJobExecutionId)));
                }
                stepExecutionIds.addAll(executionIds);
            }
            final List<ExtendedStepExecution> candidates = new ArrayList<>(stepExecutionIds.size());
            for (final byte[] stepExecutionId : stepExecutionIds) {
                final ExtendedStepExecution stepExecution = _se(jedis, stepExecutionId);
                if (stepExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", Arrays.toString(stepExecutionId)));
                }
                if (stepName.equals(stepExecution.getStepName())) {
                    candidates.add(stepExecution);
                }
            }
            return candidates.size();
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            return _se(jedis, stepExecutionId);
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final StepExecution[] executions = new StepExecution[stepExecutionIds.length];
            for (int i = 0; i < stepExecutionIds.length; ++i) {
                executions[i] = _se(jedis, stepExecutionIds[i]);
            }
            return executions;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final List<byte[]> partitionIds = _list(jedis, STEP_EXECUTION_PARTITION_EXECUTIONS_PREFIX, stepExecutionId);
            if (partitionIds.isEmpty()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            final List<PartitionExecution> ret = new ArrayList<>();
            for (final byte[] partitionExecutionId : partitionIds) {
                final PartitionExecution partitionExecution = _pe(jedis, partitionExecutionId);
                if (partitionExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", Arrays.toString(partitionExecutionId)));
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
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            final PartitionExecution partition = _pe(jedis, partitionExecutionId);
            if (partition == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            return partition;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    private ExtendedJobInstance _ji(final BinaryJedisCommands jedis, final long id) throws ClassNotFoundException, IOException {
        final byte[] response = jedis.get(marshalling.marshall(JOB_INSTANCE_PREFIX, id));
        return response == null ? null : marshalling.unmarshall(response, JobInstanceImpl.class, this.loader.get());
    }

    private ExtendedJobInstance _ji(final BinaryJedisCommands jedis, final byte[] id) throws ClassNotFoundException, IOException {
        return _ji(jedis, marshalling.unmarshall(id, Long.class, this.loader.get()));
    }

    private ExtendedJobExecution _je(final BinaryJedisCommands jedis, final long id) throws ClassNotFoundException, IOException {
        final byte[] response = jedis.get(marshalling.marshall(JOB_EXECUTION_PREFIX, id));
        return response == null ? null : marshalling.unmarshall(response, JobExecutionImpl.class, this.loader.get());
    }

    private ExtendedJobExecution _je(final BinaryJedisCommands jedis, final byte[] id) throws ClassNotFoundException, IOException {
        return _je(jedis, marshalling.unmarshall(id, Long.class, this.loader.get()));
    }

    private ExtendedStepExecution _se(final BinaryJedisCommands jedis, final long id) throws ClassNotFoundException, IOException {
        final byte[] response = jedis.get(marshalling.marshall(STEP_EXECUTION_PREFIX, id));
        return response == null ? null : marshalling.unmarshall(response, StepExecutionImpl.class, this.loader.get());
    }

    private ExtendedStepExecution _se(final BinaryJedisCommands jedis, final byte[] id) throws ClassNotFoundException, IOException {
        return _se(jedis, marshalling.unmarshall(id, Long.class, this.loader.get()));
    }

    private PartitionExecution _pe(final BinaryJedisCommands jedis, final long id) throws ClassNotFoundException, IOException {
        final byte[] response = jedis.get(marshalling.marshall(PARTITION_EXECUTION_PREFIX, id));
        return marshalling.unmarshall(response, PartitionExecutionImpl.class, this.loader.get());
    }

    private PartitionExecution _pe(final BinaryJedisCommands jedis, final byte[] id) throws ClassNotFoundException, IOException {
        return _pe(jedis, marshalling.unmarshall(id, Long.class, this.loader.get()));
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final byte[] prefix, final long id) throws IOException {
        final byte[] key = marshalling.marshall(prefix, id);
        return jedis.lrange(key, 0, jedis.llen(key));
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final byte[] prefix, final String id) throws IOException {
        final byte[] key = marshalling.marshall(prefix, id);
        return jedis.lrange(key, 0, jedis.llen(key));
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final byte[] prefix, final byte[] id) throws IOException, ClassNotFoundException {
        final byte[] key = marshalling.marshall(prefix, marshalling.unmarshall(id, this.loader.get()));
        return jedis.lrange(key, 0, jedis.llen(key));
    }
}
