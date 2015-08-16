/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.redis.repository;

import gnu.trove.set.hash.THashSet;
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

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RedisRepository implements Repository {

    protected static final String JOB_INSTANCE_ID = "ji_id";
    protected static final String JOB_EXECUTION_ID = "je_id";
    protected static final String STEP_EXECUTION_ID = "se_id";
    protected static final String PARTITION_EXECUTION_ID = "pe_id";

    protected static final String JOB_INSTANCE_PREFIX = "ji_";
    protected static final String JOB_EXECUTION_PREFIX = "je_";
    protected static final String STEP_EXECUTION_PREFIX = "se_";
    protected static final String PARTITION_EXECUTION_PREFIX = "pe_";

    protected static final String JOB_EXECUTION_HISTORY_PREFIX = "jeh_";
    protected static final String STEP_EXECUTION_PARTITION_EXECUTIONS_PREFIX = "sepe_";
    protected static final String JOB_INSTANCE_EXECUTIONS_PREFIX = "jie_";
    protected static final String JOB_NAME_JOB_INSTANCES_PREFIX = "jnji_";
    protected static final String JOB_NAME_JOB_EXECUTIONS_PREFIX = "jnje_";
    protected static final String LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX = "lje_";
    protected static final String JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX = "jese_";

    protected static final String JOB_NAMES = "jn";

    protected final WeakReference<ClassLoader> loader;
    protected final JedisShardInfo info;
    protected final Marshalling marshalling;

    protected final Jedis _open() {
        return info.createResource();
    }

    public RedisRepository(final JedisShardInfo info, final ClassLoader loader, final Marshalling marshalling) throws Exception {
        this.loader = new WeakReference<>(loader);
        this.info = info;
        this.marshalling = marshalling;
    }

    private static byte[] bytes(final String a, final long b) {
        return (a + Long.toString(b)).getBytes(UTF_8);
    }

    private static byte[] bytes(final String a, final String b) {
        return (a + b).getBytes(UTF_8);
    }

    private static byte[] bytes(final String a) {
        return a.getBytes(UTF_8);
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
            jedis.set(bytes(JOB_INSTANCE_PREFIX, id), marshalling.marshall(instance));
            jedis.sadd(JOB_NAMES.getBytes(UTF_8), marshalling.marshall(jobId));
            jedis.rpush(bytes(JOB_NAME_JOB_INSTANCES_PREFIX, jobId), marshalling.marshallLong(id));
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
        if (!jedis.exists(bytes(JOB_INSTANCE_PREFIX, jobInstanceId))) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
        }
        final long jobExecutionId = jedis.incr(bytes(JOB_EXECUTION_ID));
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setJobInstanceId(jobInstanceId)
                .setJobExecutionId(jobExecutionId)
                .setJobName(jobName)
                .setBatchStatus(BatchStatus.STARTING)
                .setJobParameters(parameters)
                .setCreateTime(timestamp)
                .setLastUpdatedTime(timestamp)
                .build();
        jedis.set(bytes(JOB_EXECUTION_PREFIX, jobExecutionId), marshalling.marshall(execution));
        final byte[] jobExecutionIdBytes = marshalling.marshallLong(jobExecutionId);
        jedis.set(
                bytes(LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX, jobInstanceId),
                jobExecutionIdBytes
        );
        jedis.rpush(
                bytes(JOB_INSTANCE_EXECUTIONS_PREFIX, jobInstanceId),
                jobExecutionIdBytes
        );
        jedis.rpush(
                bytes(JOB_NAME_JOB_EXECUTIONS_PREFIX, jobName),
                jobExecutionIdBytes
        );
        return execution;
    }

    @Override
    public StepExecutionImpl createStepExecution(final long jobExecutionId, final String stepName, final Date timestamp) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            if (!jedis.exists(bytes(JOB_EXECUTION_PREFIX, jobExecutionId))) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
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
                    bytes(STEP_EXECUTION_PREFIX, stepExecutionId),
                    marshalling.marshall(execution));
            jedis.rpush(
                    bytes(JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId),
                    marshalling.marshallLong(stepExecutionId)
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
            if (!jedis.exists(bytes(STEP_EXECUTION_PREFIX, stepExecutionId))) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
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
            jedis.set(bytes(PARTITION_EXECUTION_PREFIX, id), marshalling.marshall(execution));
            jedis.rpush(bytes(STEP_EXECUTION_PARTITION_EXECUTIONS_PREFIX, stepExecutionId), marshalling.marshallLong(id));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jedis.set(
                    bytes(JOB_EXECUTION_PREFIX, jobExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jedis.set(
                    bytes(JOB_EXECUTION_PREFIX, jobExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jedis.set(
                    bytes(JOB_EXECUTION_PREFIX, jobExecutionId),
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
            final List<byte[]> jobExecutionIds = new ArrayList<>();
            final List<byte[]> current = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId);
            if (current.isEmpty() && !_hasJobExecution(jedis, jobExecutionId)) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            jobExecutionIds.addAll(current);
            jobExecutionIds.add(marshalling.marshallLong(restartJobExecutionId));
            final List<byte[]> oldJobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, restartJobExecutionId);
            if (oldJobExecutionIds.isEmpty() && !_hasJobExecution(jedis, restartJobExecutionId)) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", restartJobExecutionId));
            }
            jobExecutionIds.addAll(oldJobExecutionIds);
            jedis.rpush(
                    bytes(JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    bytes(STEP_EXECUTION_PREFIX, stepExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    bytes(STEP_EXECUTION_PREFIX, stepExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    bytes(STEP_EXECUTION_PREFIX, stepExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            jedis.set(
                    bytes(STEP_EXECUTION_PREFIX, stepExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            jedis.set(
                    bytes(PARTITION_EXECUTION_PREFIX, partitionExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            jedis.set(
                    bytes(PARTITION_EXECUTION_PREFIX, partitionExecutionId),
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            jedis.set(
                    bytes(PARTITION_EXECUTION_PREFIX, partitionExecutionId),
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
            final Set<byte[]> names = jedis.smembers(bytes(JOB_NAMES));
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
            long count = jedis.llen(bytes(JOB_NAME_JOB_INSTANCES_PREFIX, jobName));
            if (count == 0) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
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
            final byte[] key = bytes(JOB_NAME_JOB_INSTANCES_PREFIX, jobName);
            List<byte[]> ids = jedis.lrange(key, start, finish);
            if (ids.isEmpty() && jedis.llen(key) == 0) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
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
                switch (jobExecution.getBatchStatus()) {
                    case STARTING:
                    case STARTED:
                    case STOPPING:
                        ids.add(jobExecution.getExecutionId());
                }
            }
            if (ids.isEmpty() && !_hasJobInstance(jedis, jobName)) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.repository.no.such.job", jobName));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
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
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
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
        if (executions.isEmpty() && !_hasJobInstance(jedis, jobInstanceId)) {
            throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.repository.no.such.job.instance", jobInstanceId));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            final long latest;
            try {
                latest = marshalling.unmarshallLong(jedis.get(bytes(LATEST_JOB_EXECUTION_FOR_INSTANCE_PREFIX, jobExecution.getJobInstanceId())), this.loader.get());
            } catch (final IllegalAccessException | IOException e) {
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
            if (values.isEmpty()) {
                if (!_hasJobExecution(jedis, jobExecutionId)) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
                }
                return Collections.emptyList();
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            final byte[] stepExecutionIdBytes = marshalling.marshallLong(stepExecutionId);
            final List<byte[]> stepExecutionIds = new ArrayList<>();

            List<byte[]> executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final byte[] historicJobExecutionId : historicJobExecutionIds) {
                executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", Arrays.toString(historicJobExecutionId)));
                }
                stepExecutionIds.addAll(executionIds);
            }
            for (final byte[] id : stepExecutionIds) {
                final ExtendedStepExecution stepExecution = _se(jedis, id);
                if (Arrays.equals(stepExecutionIdBytes, id)) {
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
            if(jedis != null) {
                jedis.disconnect();
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
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        Jedis jedis = null;
        try {
            jedis = _open();
            List<byte[]> historicJobExecutionIds = _list(jedis, JOB_EXECUTION_HISTORY_PREFIX, jobExecutionId);
            if (historicJobExecutionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            final List<byte[]> stepExecutionIds = new ArrayList<>();
            List<byte[]> executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            stepExecutionIds.addAll(executionIds);
            for (final byte[] historicJobExecutionId : historicJobExecutionIds) {
                executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", Arrays.toString(historicJobExecutionId)));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.repository.no.step.named", jobExecutionId, stepName));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", jobExecutionId));
            }
            final List<byte[]> stepExecutionIds = new ArrayList<>();
            for (final byte[] historicJobExecutionId : historicJobExecutionIds) {
                final List<byte[]> executionIds = _list(jedis, JOB_EXECUTIONS_STEP_EXECUTIONS_PREFIX, historicJobExecutionId);
                if (executionIds == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.repository.no.such.job.execution", Arrays.toString(historicJobExecutionId)));
                }
                stepExecutionIds.addAll(executionIds);
            }
            final List<ExtendedStepExecution> candidates = new ArrayList<>(stepExecutionIds.size());
            for (final byte[] stepExecutionId : stepExecutionIds) {
                final ExtendedStepExecution stepExecution = _se(jedis, stepExecutionId);
                if (stepExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", Arrays.toString(stepExecutionId)));
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
            final ExtendedStepExecution se = _se(jedis, stepExecutionId);
            if (se == null) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            return se;
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
                final ExtendedStepExecution se = _se(jedis, stepExecutionIds[i]);
                if (se == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionIds[i]));
                }
                executions[i] = se;
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.repository.no.such.step.execution", stepExecutionId));
            }
            final List<PartitionExecution> ret = new ArrayList<>();
            for (final byte[] partitionExecutionId : partitionIds) {
                final PartitionExecution partitionExecution = _pe(jedis, partitionExecutionId);
                if (partitionExecution == null) {
                    throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", Arrays.toString(partitionExecutionId)));
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
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.repository.no.such.partition.execution", partitionExecutionId));
            }
            return partition;
        } finally {
            if(jedis != null) {
                jedis.disconnect();
            }
        }
    }

    private ExtendedJobInstance _ji(final BinaryJedisCommands jedis, final long id) throws Exception {
        final byte[] response = jedis.get(bytes(JOB_INSTANCE_PREFIX, id));
        return response == null ? null : marshalling.unmarshall(response, JobInstanceImpl.class, this.loader.get());
    }

    private ExtendedJobInstance _ji(final BinaryJedisCommands jedis, final byte[] id) throws Exception {
        return _ji(jedis, marshalling.unmarshallLong(id, this.loader.get()));
    }

    private ExtendedJobExecution _je(final BinaryJedisCommands jedis, final long id) throws Exception {
        final byte[] response = jedis.get(bytes(JOB_EXECUTION_PREFIX, id));
        return response == null ? null : marshalling.unmarshall(response, JobExecutionImpl.class, this.loader.get());
    }

    private ExtendedJobExecution _je(final BinaryJedisCommands jedis, final byte[] id) throws Exception {
        return _je(jedis, marshalling.unmarshallLong(id, this.loader.get()));
    }

    private ExtendedStepExecution _se(final BinaryJedisCommands jedis, final long id) throws Exception {
        final byte[] response = jedis.get(bytes(STEP_EXECUTION_PREFIX, id));
        return response == null ? null : marshalling.unmarshall(response, StepExecutionImpl.class, this.loader.get());
    }

    private ExtendedStepExecution _se(final BinaryJedisCommands jedis, final byte[] id) throws Exception {
        return _se(jedis, marshalling.unmarshallLong(id, this.loader.get()));
    }

    private PartitionExecution _pe(final BinaryJedisCommands jedis, final long id) throws Exception {
        final byte[] response = jedis.get(bytes(PARTITION_EXECUTION_PREFIX, id));
        return marshalling.unmarshall(response, PartitionExecutionImpl.class, this.loader.get());
    }

    private PartitionExecution _pe(final BinaryJedisCommands jedis, final byte[] id) throws Exception {
        return _pe(jedis, marshalling.unmarshallLong(id, this.loader.get()));
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final String prefix, final long id) {
        final byte[] key = bytes(prefix, id);
        return _list(jedis, key);
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final String prefix, final String id) {
        final byte[] key = bytes(prefix, id);
        return _list(jedis, key);
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final String prefix, final byte[] id) throws Exception {
        final byte[] key = bytes(prefix, marshalling.unmarshallLong(id, this.loader.get()));
        return _list(jedis, key);
    }

    private List<byte[]> _list(final BinaryJedisCommands jedis, final byte[] key) {
        final long len = jedis.llen(key);
        if (len == 0) {
            return Collections.emptyList();
        }
        final List<byte[]> ret = jedis.lrange(key, 0, len);
        assert ret != null;
        return ret;
    }

    private boolean _hasJobExecution(final BinaryJedisCommands jedis, final long jobExecutionId) throws Exception {
        return jedis.get(bytes(JOB_EXECUTION_PREFIX, jobExecutionId)) != null;
    }

    private boolean _hasJobInstance(final BinaryJedisCommands jedis, final long jobInstanceId) throws Exception {
        return jedis.get(bytes(JOB_INSTANCE_PREFIX, jobInstanceId)) != null;
    }

    private boolean _hasJobInstance(final BinaryJedisCommands jedis, final String jobName) throws Exception {
        return jedis.llen(bytes(JOB_NAME_JOB_INSTANCES_PREFIX, jobName)) != 0;
    }
}
