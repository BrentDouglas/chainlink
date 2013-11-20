package io.machinecode.nock.core.local;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.core.impl.JobExecutionImpl;
import io.machinecode.nock.core.impl.JobInstanceImpl;
import io.machinecode.nock.core.impl.StepExecutionImpl;
import io.machinecode.nock.spi.Checkpoint;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.RestartableJobExecution;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Step;

import javax.batch.operations.JobExecutionNotMostRecentException;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalRepository implements ExecutionRepository {
    protected final AtomicLong jobInstanceIndex = new AtomicLong();
    protected final AtomicLong jobExecutionIndex = new AtomicLong();
    protected final AtomicLong stepExecutionIndex = new AtomicLong();

    protected final TLongObjectMap<JobInstance> jobInstances = new TLongObjectHashMap<JobInstance>();
    protected final TLongObjectMap<RestartableJobExecution> jobExecutions = new TLongObjectHashMap<RestartableJobExecution>();
    protected final TLongObjectMap<List<Long>> jobInstanceExecutions = new TLongObjectHashMap<List<Long>>();
    protected final TLongObjectMap<JobInstance> jobExecutionInstances = new TLongObjectHashMap<JobInstance>();
    protected final TLongObjectMap<StepExecution> stepExecutions = new TLongObjectHashMap<StepExecution>();
    protected final TLongObjectMap<Checkpoint> checkpoints = new TLongObjectHashMap<Checkpoint>();
    protected final TLongObjectMap<Set<Long>> jobExecutionStepExecutions = new TLongObjectHashMap<Set<Long>>();
    protected final TLongObjectMap<Long> latestJobExecutionForInstance = new TLongObjectHashMap<Long>();

    protected final AtomicBoolean jobInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobInstanceExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean stepExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean checkpointLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionStepExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean latestJobExecutionForInstanceLock = new AtomicBoolean(false);

    @Override
    public JobInstance createJobInstance(final Job job) {
        final long id = jobInstanceIndex.incrementAndGet();
        final JobInstanceImpl instance = new JobInstanceImpl.Builder()
                .setInstanceId(id)
                .setJobName(job.getId())
                .build();
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            jobInstances.put(id, instance);
        } finally {
            jobInstanceLock.set(false);
        }
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            jobInstanceExecutions.put(id, new ArrayList<Long>(0));
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        return instance;
    }

    @Override
    public RestartableJobExecution createJobExecution(final JobInstance instance) throws NoSuchJobInstanceException {
        final long id = jobExecutionIndex.incrementAndGet();
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setExecutionId(id)
                .setJobName(instance.getJobName())
                .setBatchStatus(BatchStatus.STARTING)
                .setCreated(new Date())
                .setUpdated(new Date())
                .build();
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            jobExecutions.put(id, execution);
        } finally {
            jobExecutionLock.set(false);
        }
        while (!jobExecutionInstanceLock.compareAndSet(false, true)) {}
        try {
            jobExecutionInstances.put(id, instance);
        } finally {
            jobExecutionInstanceLock.set(false);
        }
        while (!latestJobExecutionForInstanceLock.compareAndSet(false, true)) {}
        try {
            latestJobExecutionForInstance.put(instance.getInstanceId(), id);
        } finally {
            latestJobExecutionForInstanceLock.set(false);
        }
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<Long> executions = jobInstanceExecutions.get(instance.getInstanceId());
            if (executions == null) {
                throw new NoSuchJobInstanceException();
            }
            executions.add(id);
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            jobExecutionStepExecutions.put(id, new THashSet<Long>(0));
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        return execution;
    }

    @Override
    public StepExecution createStepExecution(final JobExecution jobExecution, final Step<?, ?> step) {
        final long id = stepExecutionIndex.incrementAndGet();
        final StepExecutionImpl execution = new StepExecutionImpl.Builder()
                .setExecutionId(id)
                .setStepName(step.getId())
                .setBatchStatus(BatchStatus.STARTING)
                .build();
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            stepExecutions.putIfAbsent(id, execution);
            //final StepExecution old = stepExecutions.putIfAbsent(id, execution);
            //if (old != null) {
            //    throw new StepAlreadyExistsException();
            //}
        } finally {
            stepExecutionLock.set(false);
        }
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            final Set<Long> executionIds = jobExecutionStepExecutions.get(jobExecution.getExecutionId());
            if (executionIds == null) {
                throw new NoSuchJobExecutionException();
            }
            executionIds.add(id);
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        return execution;
    }

    @Override
    public void startJobExecution(final long executionId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final RestartableJobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            jobExecutions.put(executionId, JobExecutionImpl.from(execution)
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
    public void updateJobExecution(final long executionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final RestartableJobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            jobExecutions.put(executionId, JobExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setBatchStatus(batchStatus)
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void finishJobExecution(final long executionId, final BatchStatus batchStatus, final String exitStatus, final String restartId, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final RestartableJobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            jobExecutions.put(executionId, JobExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setRestartId(restartId)
                    .setUpdated(timestamp)
                    .setEnd(timestamp)
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void startStepExecution(final long stepExecutionId, final Metric[] metrics, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setStart(timestamp)
                    .setMetrics(metrics)
                    .setBatchStatus(BatchStatus.STARTED)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(batchStatus.name())
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Serializable serializable, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(stepExecutionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setPersistentUserData(serializable)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long stepExecutionId, final Serializable serializable, final Metric[] metrics, final Checkpoint checkpoint, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(stepExecutionId);
             if (execution == null) {
                 throw new NoSuchJobExecutionException();
             }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setPersistentUserData(serializable)
                    .setMetrics(metrics)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
        while (!checkpointLock.compareAndSet(false, true)) {}
        try {
            checkpoints.put(stepExecutionId, checkpoint);
        } finally {
            checkpointLock.set(false);
        }
    }

    @Override
    public void finishStepExecution(final long stepExecutionId, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(stepExecutionId);
             if (execution == null) {
                 throw new NoSuchJobExecutionException();
             }
            stepExecutions.put(stepExecutionId, StepExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setEnd(timestamp)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public Checkpoint getStepExecutionCheckpoint(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!checkpointLock.compareAndSet(false, true)) {}
        try {
            return checkpoints.get(stepExecutionId);
        } finally {
            checkpointLock.set(false);
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
                 throw new NoSuchJobException();
            }
            return ret;
        } finally {
            jobInstanceLock.set(false);
        }
    }

    //TODO Pretty sure this is wrong also needs to check start & count against list size
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
        Collections.reverse(ret);
        final int all = ret.size();
        if (start >= all) {
            return Collections.emptyList();
        }
        final int diff = all - start;
        if (diff < 0) {
            return Collections.emptyList();
        }
        return ret.subList(start, start + (count > diff ? count : diff));
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
                throw new NoSuchJobException();
            }
            return ids;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public Properties getParameters(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final JobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            return execution.getJobParameters();
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public JobInstance getJobInstance(final long instanceId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            final JobInstance instance = jobInstances.get(instanceId);
            if (instance == null) {
                throw new NoSuchJobInstanceException();
            }
            return instance;
        } finally {
            jobInstanceLock.set(false);
        }
    }

    @Override
    public JobInstance getJobInstanceForExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionInstanceLock.compareAndSet(false, true)) {}
        try {
            final JobInstance instance = jobExecutionInstances.get(executionId);
            if (instance == null) {
                throw new NoSuchJobInstanceException();
            }
            return instance;
        } finally {
            jobExecutionInstanceLock.set(false);
        }
    }

    @Override
    public List<JobExecution> getJobExecutions(final JobInstance instance) throws NoSuchJobInstanceException, JobSecurityException {
        final List<Long> executionIds;
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            executionIds = jobInstanceExecutions.get(instance.getInstanceId());
            if (executionIds == null) {
                throw new NoSuchJobInstanceException();
            }
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<JobExecution> executions = new ArrayList<JobExecution>(executionIds.size());
            for (final Long executionId : executionIds) {
                executions.add(jobExecutions.get(executionId));
            }
            return executions;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public RestartableJobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final RestartableJobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            return execution;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public RestartableJobExecution getLatestJobExecution(final long executionId) throws NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobSecurityException {
        final JobInstance instance;
        while (!jobExecutionInstanceLock.compareAndSet(false, true)) {}
        try {
            instance = jobExecutionInstances.get(executionId);
            if (instance == null) {
                throw new NoSuchJobExecutionException();
            }
        } finally {
            jobExecutionInstanceLock.set(false);
        }

        final Long latest;
        while (!latestJobExecutionForInstanceLock.compareAndSet(false, true)) {}
        try {
            latest = latestJobExecutionForInstance.get(instance.getInstanceId());
            if (latest == null) {
                throw new JobExecutionNotMostRecentException();
            }
        } finally {
            latestJobExecutionForInstanceLock.set(false);
        }
        return getJobExecution(latest);
    }

    @Override
    public List<StepExecution> getStepExecutions(final long jobExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
        final Set<Long> executionIds;
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException();
            }
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<StepExecution> executions = new ArrayList<StepExecution>(executionIds.size());
            for (final Long executionId : executionIds) {
                executions.add(stepExecutions.get(executionId));
            }
            return executions;
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public StepExecution getStepExecution(final long jobExecutionId, final String stepName) throws NoSuchJobExecutionException, JobSecurityException {
        final Set<Long> executionIds;
        while (!jobExecutionStepExecutionLock.compareAndSet(false, true)) {}
        try {
            executionIds = jobExecutionStepExecutions.get(jobExecutionId);
            if (executionIds == null) {
                throw new NoSuchJobExecutionException();
            }
        } finally {
            jobExecutionStepExecutionLock.set(false);
        }
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            for (final Long executionId : executionIds) {
                final StepExecution execution = stepExecutions.get(executionId);
                if (stepName.equals(execution.getStepName())) {
                    return execution;
                }
            }
            throw new NoSuchJobExecutionException();
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public StepExecution getStepExecution(final long stepExecutionId) throws NoSuchJobExecutionException, JobSecurityException {
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
                for (final long executionId : stepExecutionIds) {
                    executions[i] = stepExecutions.get(executionId);
                }
            }
            return executions;
        } finally {
            stepExecutionLock.set(false);
        }
    }
}
