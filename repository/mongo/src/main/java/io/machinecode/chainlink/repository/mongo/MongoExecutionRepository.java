package io.machinecode.chainlink.repository.mongo;

import com.mongodb.DBObject;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import io.machinecode.chainlink.spi.util.Messages;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.jongo.ResultHandler;

import javax.batch.operations.JobExecutionAlreadyCompleteException;
import javax.batch.operations.JobExecutionNotMostRecentException;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobInstance;
import javax.batch.runtime.Metric;
import javax.batch.runtime.StepExecution;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import static javax.batch.runtime.BatchStatus.FAILED;
import static javax.batch.runtime.BatchStatus.STARTED;
import static javax.batch.runtime.BatchStatus.STARTING;
import static javax.batch.runtime.BatchStatus.STOPPED;
import static javax.batch.runtime.BatchStatus.STOPPING;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoExecutionRepository implements ExecutionRepository {

    protected static final String IDS = "ids";
    protected static final String JOB_INSTANCE_ID = "job_instance_id";
    protected static final String JOB_EXECUTION_ID = "job_execution_id";
    protected static final String STEP_EXECUTION_ID = "step_execution_id";
    protected static final String PARTITION_EXECUTION_ID = "partition_execution_id";

    protected static final String JOB_INSTANCES = "job_instances";
    protected static final String JOB_EXECUTIONS = "job_executions";
    protected static final String STEP_EXECUTIONS = "step_executions";
    protected static final String PARTITION_EXECUTIONS = "partition_executions";

    public static final ResultHandler<Void> DO_NOTHING = new ResultHandler<Void>() {
        @Override
        public Void map(final DBObject result) {
            return null;
        }
    };

    protected final Jongo jongo;
    protected final Marshalling marshalling;
    //protected final boolean camel;

    public MongoExecutionRepository(final Jongo jongo, final Marshalling marshalling, final boolean camel) {
        this.jongo = jongo;
        this.marshalling = marshalling;
        //this.camel = camel;
    }

    protected long _id(final String key) {
        return jongo.getCollection(IDS)
                .findAndModify("{ _id: # }", key)
                .with("{ $inc: { seq: 1 } }")
                .returnNew()
                .upsert()
                .map(new Handler<Integer>("seq"));
    }

    @Override
    public ExtendedJobInstance createJobInstance(final String jobId, final String jslName, final Date timestamp) {
        final MongoJobInstance instance = new MongoJobInstance.Builder()
                .setJobInstanceId(_id(JOB_INSTANCE_ID))
                .setJobName(jobId)
                .setJslName(jslName)
                .setCreateTime(timestamp)
                .build();
        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        jobInstances.insert(instance);
        return instance;
    }

    @Override
    public ExtendedJobExecution createJobExecution(final ExtendedJobInstance jobInstance, final Properties parameters, final Date timestamp) {
        final long jobExecutionId = _id(JOB_EXECUTION_ID);
        final MongoJobExecution execution = new MongoJobExecution.Builder()
                .setJobInstanceId(jobInstance.getInstanceId())
                .setJobExecutionId(jobExecutionId)
                .setJobName(jobInstance.getJobName())
                .setBatchStatus(STARTING)
                .setJobParameters(parameters)
                .setCreateTime(timestamp)
                .setLastUpdatedTime(timestamp)
                .build();
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        jobExecutions.insert(execution);

        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        jobInstances.findAndModify("{" + Fields.JOB_INSTANCE_ID + ":#}", jobInstance.getInstanceId())
                .with("{$set:{" + Fields.LATEST_JOB_EXECUTION_ID + ":#}}", jobExecutionId)
                .upsert()
                .map(DO_NOTHING);

        return execution;
    }

    @Override
    public ExtendedStepExecution createStepExecution(final ExtendedJobExecution jobExecution, final String stepName, final Date timestamp) {
        final long stepExecutionId = _id(STEP_EXECUTION_ID);
        final MongoStepExecution execution = new MongoStepExecution.Builder()
                .setJobExecutionId(jobExecution.getExecutionId())
                .setStepExecutionId(stepExecutionId)
                .setStepName(stepName)
                .setCreateTime(timestamp)
                .setUpdatedTime(timestamp)
                .setBatchStatus(STARTING)
                .setMetrics(MongoMetric.empty())
                .build();
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        stepExecutions.insert(execution);

        return execution;
    }

    @Override
    public PartitionExecution createPartitionExecution(final long stepExecutionId, final int partitionId, final Properties properties, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws IOException, ClassNotFoundException {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final long partitionExecutionId = _id(PARTITION_EXECUTION_ID);
        final MongoPartitionExecution execution = new MongoPartitionExecution.Builder()
                .setPartitionExecutionId(partitionExecutionId)
                .setStepExecutionId(stepExecutionId)
                .setPartitionId(partitionId)
                .setPartitionParameters(properties)
                .setCreateTime(timestamp)
                .setUpdatedTime(timestamp)
                .setPersistentUserData(clonedPersistentUserData)
                .setReaderCheckpoint(clonedReaderCheckpoint)
                .setWriterCheckpoint(clonedWriterCheckpoint)
                .setMetrics(MongoMetric.empty())
                .setBatchStatus(STARTING)
                .build();
        final MongoCollection partitionExecutions = jongo.getCollection(PARTITION_EXECUTIONS);
        partitionExecutions.insert(execution);
        return execution;
    }

    @Override
    public void startJobExecution(final long jobExecutionId, final Date timestamp) throws NoSuchJobExecutionException, IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final String query = "{" + Fields.JOB_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find(query, jobExecutionId).as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final MongoJobExecution execution = cursor.next();
            jobExecutions.update(query, jobExecutionId).with(MongoJobExecution.from(execution)
                    .setLastUpdatedTime(timestamp)
                    .setStartTime(timestamp)
                    .setBatchStatus(STARTED)
                    .build()
            );
        }
    }

    @Override
    public void updateJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final String query = "{" + Fields.JOB_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find(query, jobExecutionId).as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final MongoJobExecution execution = cursor.next();
            jobExecutions.update(query, jobExecutionId).with(MongoJobExecution.from(execution)
                    .setLastUpdatedTime(timestamp)
                    .setBatchStatus(batchStatus)
                    .build()
            );
        }
    }

    @Override
    public void finishJobExecution(final long jobExecutionId, final BatchStatus batchStatus, final String exitStatus, final String restartElementId, final Date timestamp) throws NoSuchJobExecutionException, IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final String query = "{" + Fields.JOB_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find(query, jobExecutionId).as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final MongoJobExecution execution = cursor.next();
            jobExecutions.update(query, jobExecutionId).with(MongoJobExecution.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setRestartElementId(restartElementId)
                    .setLastUpdatedTime(timestamp)
                    .setEndTime(timestamp)
                    .build()
            );
        }
    }

    @Override
    public void linkJobExecutions(final long jobExecutionId, final long restartJobExecutionId) throws NoSuchJobExecutionException, IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final Set<Long> oldJobExecutionIds = _jobExecutionHistory(restartJobExecutionId, jobExecutions);
        final Set<Long> jobExecutionIds = _jobExecutionHistory(jobExecutionId, jobExecutions);
        jobExecutionIds.add(restartJobExecutionId);
        jobExecutionIds.addAll(oldJobExecutionIds);

        jobExecutions.update("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .with("{$set:{" + Fields.PREVIOUS_JOB_EXECUTION_IDS + ":#}}", jobExecutionIds);
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Date timestamp) throws NoSuchJobExecutionException, IOException {
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        final String query = "{" + Fields.STEP_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find(query, stepExecutionId).as(MongoStepExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            final MongoStepExecution execution = cursor.next();
            stepExecutions.update(query, stepExecutionId).with(MongoStepExecution.from(execution)
                    .setStartTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setBatchStatus(STARTED)
                    .build()
            );
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Date timestamp) throws NoSuchJobExecutionException, IOException, ClassNotFoundException {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        final String query = "{" + Fields.STEP_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find(query, stepExecutionId).as(MongoStepExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            final MongoStepExecution execution = cursor.next();
            stepExecutions.update(query, stepExecutionId).with(MongoStepExecution.from(execution)
                    .setUpdatedTime(timestamp)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MongoMetric.copy(metrics))
                    .build()
            );
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws NoSuchJobExecutionException, IOException, ClassNotFoundException {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        final String query = "{" + Fields.STEP_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find(query, stepExecutionId).as(MongoStepExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            final MongoStepExecution execution = cursor.next();
            stepExecutions.update(query, stepExecutionId).with(MongoStepExecution.from(execution)
                    .setUpdatedTime(timestamp)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MongoMetric.copy(metrics))
                    .setReaderCheckpoint(clonedReaderCheckpoint)
                    .setWriterCheckpoint(clonedWriterCheckpoint)
                    .build()
            );
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final Metric[] metrics, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, IOException {
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        final String query = "{" + Fields.STEP_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find(query, stepExecutionId).as(MongoStepExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            final MongoStepExecution execution = cursor.next();
            stepExecutions.update(query, stepExecutionId).with(MongoStepExecution.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setUpdatedTime(timestamp)
                    .setEndTime(timestamp)
                    .setMetrics(MongoMetric.copy(metrics))
                    .build()
            );
        }
    }

    @Override
    public void startPartitionExecution(final long partitionExecutionId, final Date timestamp) throws NoSuchJobExecutionException, IOException {
        final MongoCollection partitionExecutions = jongo.getCollection(PARTITION_EXECUTIONS);
        final String query = "{" + Fields.PARTITION_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoPartitionExecution> cursor = partitionExecutions.find(query, partitionExecutionId).as(MongoPartitionExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            final MongoPartitionExecution partition = cursor.next();
            partitionExecutions.update(query, partitionExecutionId).with(MongoPartitionExecution.from(partition)
                    .setUpdatedTime(timestamp)
                    .setStartTime(timestamp)
                    .setBatchStatus(STARTED)
                    .build()
            );
        }
    }

    @Override
    public void updatePartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final Serializable readerCheckpoint, final Serializable writerCheckpoint, final Date timestamp) throws NoSuchJobExecutionException, ClassNotFoundException, IOException {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final Serializable clonedReaderCheckpoint = marshalling.clone(readerCheckpoint);
        final Serializable clonedWriterCheckpoint = marshalling.clone(writerCheckpoint);
        final MongoCollection partitionExecutions = jongo.getCollection(PARTITION_EXECUTIONS);
        final String query = "{" + Fields.PARTITION_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoPartitionExecution> cursor = partitionExecutions.find(query, partitionExecutionId).as(MongoPartitionExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            final MongoPartitionExecution partition = cursor.next();
            partitionExecutions.update(query, partitionExecutionId).with(MongoPartitionExecution.from(partition)
                    .setUpdatedTime(timestamp)
                    .setReaderCheckpoint(clonedReaderCheckpoint)
                    .setWriterCheckpoint(clonedWriterCheckpoint)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MongoMetric.copy(metrics))
                    .build()
            );
        }
    }

    @Override
    public void finishPartitionExecution(final long partitionExecutionId, final Metric[] metrics, final Serializable persistentUserData, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, ClassNotFoundException, IOException {
        final Serializable clonedPersistentUserData = marshalling.clone(persistentUserData);
        final MongoCollection partitionExecutions = jongo.getCollection(PARTITION_EXECUTIONS);
        final String query = "{" + Fields.PARTITION_EXECUTION_ID + ":#}";
        try (final MongoCursor<MongoPartitionExecution> cursor = partitionExecutions.find(query, partitionExecutionId).as(MongoPartitionExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            final MongoPartitionExecution partition = cursor.next();
            partitionExecutions.update(query, partitionExecutionId).with(MongoPartitionExecution.from(partition)
                    .setEndTime(timestamp)
                    .setUpdatedTime(timestamp)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setPersistentUserData(clonedPersistentUserData)
                    .setMetrics(MongoMetric.copy(metrics))
                    .build()
            );
        }
    }

    @Override
    public Set<String> getJobNames() throws IOException {
        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        final Set<String> ret = new THashSet<>();
        try (final MongoCursor<String> cursor = jobInstances.find("{}")
                .projection("{_id:0," + Fields.JOB_NAME + ":1}")
                .map(new Handler<String>(Fields.JOB_NAME))) {
            while (cursor.hasNext()) {
                ret.add(cursor.next());
            }
        }
        return ret;
    }

    @Override
    public int getJobInstanceCount(final String jobName) throws NoSuchJobException {
        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        final int ret = (int)jobInstances.count("{" + Fields.JOB_NAME + ":#}", jobName);
        if (ret == 0) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
        }
        return ret;
    }

    @Override
    public List<JobInstance> getJobInstances(final String jobName, final int start, final int count) throws NoSuchJobException, IOException {
        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        try (final MongoCursor<MongoJobInstance> cursor = jobInstances.find("{" + Fields.JOB_NAME + ":#}", jobName)
                .skip(start)
                .limit(count)
                .as(MongoJobInstance.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            final List<JobInstance> ret = new ArrayList<>(cursor.count());
            for (final MongoJobInstance instance : cursor) {
                ret.add(instance);
            }
            return ret;
            /*
            Collections.reverse(ret);
            final int size = ret.size();
            if (start >= size) {
                return Collections.emptyList();
            }
            if (start + count > size) {
                return Collections.emptyList();
            }
            return ret.subList(start, start + count);
            */
        }
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final List<Long> ret = new ArrayList<>();
        try (final MongoCursor<Long> cursor = jobExecutions.find("{" + Fields.JOB_NAME + ":#," + Fields.BATCH_STATUS + ":{$in:[#,#]}}", jobName, STARTING, STARTED)
                .projection("{" + Fields.JOB_EXECUTION_ID + ":1,_id:0}")
                .map(new Handler<Long>(Fields.JOB_EXECUTION_ID))) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobException(Messages.format("CHAINLINK-006000.execution.repository.no.such.job", jobName));
            }
            while (cursor.hasNext()) {
                ret.add(cursor.next());
            }
        }
        return ret;
    }

    @Override
    public Properties getParameters(final long jobExecutionId) throws NoSuchJobExecutionException, IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return cursor.next().getJobParameters();
        }
    }

    @Override
    public ExtendedJobInstance getJobInstance(final long jobInstanceId) throws IOException, NoSuchJobInstanceException {
        return _jobInstance(jobInstanceId, jongo.getCollection(JOB_INSTANCES));
    }

    @Override
    public ExtendedJobInstance getJobInstanceForExecution(final long jobExecutionId) throws NoSuchJobExecutionException, NoSuchJobInstanceException, IOException {
        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        final Long jobInstanceId = _jobInstanceId(jobExecutionId, jongo.getCollection(JOB_EXECUTIONS));
        return _jobInstance(jobInstanceId, jobInstances);
    }

    @Override
    public List<? extends MongoJobExecution> getJobExecutions(final long jobInstanceId) throws Exception {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find("{" + Fields.JOB_INSTANCE_ID + ":#}", jobInstanceId)
                .as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                //TODO Wrong exception maybe
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
                //throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            final List<MongoJobExecution> ret = new ArrayList<>(cursor.count());
            for (final MongoJobExecution that : cursor) {
                ret.add(that);
            }
            return ret;
        }
    }

    @Override
    public ExtendedJobExecution getJobExecution(final long jobExecutionId) throws Exception {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return cursor.next();
        }
    }

    @Override
    public ExtendedJobExecution restartJobExecution(final long jobExecutionId, final Properties parameters) throws Exception {
        final MongoCollection jobInstances = jongo.getCollection(JOB_INSTANCES);
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final Long jobInstanceId = _jobInstanceId(jobExecutionId, jobExecutions);
        try (final MongoCursor<Long> cursor = jobInstances.find("{" + Fields.JOB_INSTANCE_ID + ":#}", jobInstanceId)
                .projection("{" + Fields.LATEST_JOB_EXECUTION_ID + ":1,_id:0}")
                .map(new Handler<Long>(Fields.LATEST_JOB_EXECUTION_ID))) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
            }
            if (jobExecutionId != cursor.next()) {
                throw new JobExecutionNotMostRecentException(Messages.format("CHAINLINK-006004.repository.not.most.recent.execution", jobExecutionId, jobInstanceId));
            }
        }
        final MongoJobExecution jobExecution = _jobExecution(jobExecutionId, jobExecutions);
        switch (jobExecution.getBatchStatus()) {
            case STOPPED:
            case FAILED:
                break;
            case COMPLETED:
                throw new JobExecutionAlreadyCompleteException(Messages.format("CHAINLINK-006006.execution.repository.execution.already.complete", jobExecutionId));
            default:
                throw new JobRestartException(Messages.format("CHAINLINK-006007.execution.repository.execution.not.eligible.for.restart", jobExecution.getExecutionId(), STOPPED, FAILED, jobExecution.getBatchStatus()));
        }
        return createJobExecution(
                _jobInstance(jobInstanceId, jobInstances),
                parameters,
                new Date()
        );
    }

    @Override
    public List<? extends StepExecution> getStepExecutionsForJobExecution(final long jobExecutionId) throws Exception {
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .as(MongoStepExecution.class)) {
            final List<MongoStepExecution> ret = new ArrayList<>(cursor.count());
            for (final MongoStepExecution that : cursor) {
                ret.add(that);
            }
            return ret;
        }
    }

    @Override
    public ExtendedStepExecution getStepExecution(final long stepExecutionId) throws Exception {
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find("{" + Fields.STEP_EXECUTION_ID + ":#}", stepExecutionId)
                .as(MongoStepExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            return cursor.next();
        }
    }

    @Override
    public ExtendedStepExecution getPreviousStepExecution(final long jobExecutionId, final long stepExecutionId, final String stepName) throws Exception {
        final Set<MongoStepExecution> stepExecutions = _stepExecutionHistory(jobExecutionId);
        Date currentStepExecutionCreateTime = null;
        final List<ExtendedStepExecution> candidates = new ArrayList<>();
        for (final MongoStepExecution stepExecution : stepExecutions) {
            if (stepExecutionId == stepExecution.getStepExecutionId()) {
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
            final Date candidateCreateTime = candidate.getCreateTime();
            if (currentStepExecutionCreateTime.before(candidateCreateTime)) {
                continue;
            }
            if (latest == null) {
                latest = candidate;
                continue;
            }
            if (candidateCreateTime.after(latest.getCreateTime())) {
                latest = candidate;
            } else if (candidateCreateTime.equals(latest.getCreateTime())) {
                if (candidate.getStepExecutionId() > latest.getStepExecutionId()) {
                    latest = candidate;
                }
            }
        }
        if (latest == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    @Override
    public ExtendedStepExecution getLatestStepExecution(final long jobExecutionId, final String stepName) throws Exception {
        final Set<MongoStepExecution> stepExecutions = _stepExecutionHistory(jobExecutionId);
        final List<ExtendedStepExecution> candidates = new ArrayList<>();
        for (final MongoStepExecution execution : stepExecutions) {
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
            } else if (candidate.getCreateTime().equals(latest.getCreateTime())) {
                if (candidate.getStepExecutionId() > latest.getStepExecutionId()) {
                    latest = candidate;
                }
            }
        }
        if (latest == null) {
            throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006005.execution.repository.no.step.named", jobExecutionId, stepName));
        }
        return latest;
    }

    private Set<Long> _jobExecutionHistory(final long jobExecutionId, final MongoCollection jobExecutions) throws IOException, NoSuchJobExecutionException {
        final Set<Long> jobExecutionIds = new THashSet<>();
        try (final MongoCursor<List<Long>> c = jobExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .projection("{" + Fields.PREVIOUS_JOB_EXECUTION_IDS + ":1,_id:0}")
                .map(new Handler<List<Long>>(Fields.PREVIOUS_JOB_EXECUTION_IDS))) {
            if (!c.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            while (c.hasNext()) {
                jobExecutionIds.addAll(c.next());
            }
            return jobExecutionIds;
        }
    }

    protected Set<MongoStepExecution> _stepExecutionHistory(final long jobExecutionId) throws IOException {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final Set<Long> historicJobExecutionIds = _jobExecutionHistory(jobExecutionId, jobExecutions);

        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        final Set<MongoStepExecution> ret = new THashSet<>();
        try (final MongoCursor<MongoStepExecution> sec = stepExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .as(MongoStepExecution.class)) {
            if (!sec.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            while (sec.hasNext()) {
                ret.add(sec.next());
            }
        }

        final Object[] params = new Object[historicJobExecutionIds.size() + 1];
        final StringBuilder query = new StringBuilder("{").append(Fields.JOB_EXECUTION_ID).append(":{$in:[");
        int i = 0;
        for (final Long id : historicJobExecutionIds) {
            params[i] = id;
            query.append("#").append(",");
            ++i;
        }
        query.append("#]}}");
        params[historicJobExecutionIds.size()] = jobExecutionId;

        try (final MongoCursor<MongoStepExecution> hc = stepExecutions.find(query.toString(), params)
                .as(MongoStepExecution.class)) {
            while (hc.hasNext()) {
                ret.add(hc.next());
            }
        }
        return ret;
    }

    @Override
    public int getStepExecutionCount(final long jobExecutionId, final String stepName) throws Exception {
        final MongoCollection jobExecutions = jongo.getCollection(JOB_EXECUTIONS);
        final Set<Long> jobExecutionIds = _jobExecutionHistory(jobExecutionId, jobExecutions);
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        final int len = jobExecutionIds.size();
        final Object[] params = new Object[len + 1];
        final StringBuilder query = new StringBuilder("{").append(Fields.JOB_EXECUTION_ID).append(":{$in:[");
        int i = 0;
        for (final Long id : jobExecutionIds) {
            params[i] = id;
            query.append("#");
            ++i;
            if (i != len) {
                query.append(",");
            }
        }
        query.append("]},").append(Fields.STEP_NAME).append(":#}");
        params[jobExecutionIds.size()] = stepName;
        return (int)stepExecutions.count(query.toString(), params);
    }

    @Override
    public StepExecution[] getStepExecutions(final long[] stepExecutionIds) throws Exception {
        final StringBuilder query = new StringBuilder("{")
                .append(Fields.STEP_EXECUTION_ID)
                .append(":{$in:[");
        final Object[] params = new Object[stepExecutionIds.length];
        for (int i = 0; i < stepExecutionIds.length; ++i) {
            params[i] = stepExecutionIds[i];
            query.append("#");
            if (i != stepExecutionIds.length - 1) {
                query.append(",");
            }
        }
        query.append("]}}");
        final MongoCollection stepExecutions = jongo.getCollection(STEP_EXECUTIONS);
        try (final MongoCursor<MongoStepExecution> cursor = stepExecutions.find(query.toString(), params).as(MongoStepExecution.class)) {
            final StepExecution[] ret = new StepExecution[cursor.count()];
            for (int i = 0; i < cursor.count(); ++i) {
                ret[i] = cursor.next();
            }
            return ret;
        }
    }

    @Override
    public PartitionExecution[] getUnfinishedPartitionExecutions(final long stepExecutionId) throws Exception {
        final MongoCollection partitionExecutons = jongo.getCollection(PARTITION_EXECUTIONS);
        try (final MongoCursor<MongoPartitionExecution> cursor = partitionExecutons
                .find("{" + Fields.STEP_EXECUTION_ID + ":#," + Fields.BATCH_STATUS + ":{$in:[#,#,#,#,#]}}", stepExecutionId, FAILED, STOPPED, STOPPING, STARTED, STARTING)
                .as(MongoPartitionExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006003.execution.repository.no.such.step.execution", stepExecutionId));
            }
            final PartitionExecution[] ret = new PartitionExecution[cursor.count()];
            for (int i = 0; i < cursor.count(); ++i) {
                ret[i] = cursor.next();
            }
            return ret;
        }
    }

    @Override
    public PartitionExecution getPartitionExecution(final long partitionExecutionId) throws Exception {
        final MongoCollection partitionExecutions = jongo.getCollection(PARTITION_EXECUTIONS);
        try (final MongoCursor<MongoPartitionExecution> cursor = partitionExecutions
                .find("{" + Fields.PARTITION_EXECUTION_ID + ":#}", partitionExecutionId)
                .as(MongoPartitionExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006008.execution.repository.no.such.partition.execution", partitionExecutionId));
            }
            return cursor.next();
        }
    }

    private Long _jobInstanceId(final long jobExecutionId, final MongoCollection jobExecutions) throws NoSuchJobExecutionException, IOException {
        try (final MongoCursor<Long> jec = jobExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .projection("{" + Fields.JOB_INSTANCE_ID + ":1,_id:0}")
                .map(new Handler<Long>(Fields.JOB_INSTANCE_ID))) {
            if (!jec.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return jec.next();
        }
    }

    private MongoJobExecution _jobExecution(final long jobExecutionId, final MongoCollection jobExecutions) throws NoSuchJobExecutionException, IOException {
        try (final MongoCursor<MongoJobExecution> cursor = jobExecutions.find("{" + Fields.JOB_EXECUTION_ID + ":#}", jobExecutionId)
                .as(MongoJobExecution.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobExecutionException(Messages.format("CHAINLINK-006002.execution.repository.no.such.job.execution", jobExecutionId));
            }
            return cursor.next();
        }
    }

    public MongoJobInstance _jobInstance(final long jobInstanceId, final MongoCollection jobInstances) throws IOException, NoSuchJobInstanceException {
        try (final MongoCursor<MongoJobInstance> cursor = jobInstances.find("{" + Fields.JOB_INSTANCE_ID + ":#}", jobInstanceId)
                .as(MongoJobInstance.class)) {
            if (!cursor.hasNext()) {
                throw new NoSuchJobInstanceException(Messages.format("CHAINLINK-006001.execution.repository.no.such.job.instance", jobInstanceId));
            }
            return cursor.next();
        }
    }
}
