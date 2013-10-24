package io.machinecode.nock.core.local;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.core.impl.JobExecutionImpl;
import io.machinecode.nock.core.impl.JobInstanceImpl;
import io.machinecode.nock.core.impl.StepExecutionImpl;
import io.machinecode.nock.spi.ExecutionRepository;
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

    protected final TMap<Long, JobInstance> jobInstances = new THashMap<Long, JobInstance>();
    protected final TMap<Long, JobExecution> jobExecutions = new THashMap<Long, JobExecution>();
    protected final TMap<Long, List<JobExecution>> jobInstanceExecutions = new THashMap<Long, List<JobExecution>>();
    protected final TMap<Long, JobInstance> jobExecutionInstances = new THashMap<Long, JobInstance>();
    protected final TMap<Long, StepExecution> stepExecutions = new THashMap<Long, StepExecution>();
    protected final TMap<Long, Set<Long>> jobExecutionStepExecutions = new THashMap<Long, Set<Long>>();
    protected final TMap<Long, JobExecution> latestJobExecutionForInstance = new THashMap<Long, JobExecution>();

    protected final AtomicBoolean jobInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobInstanceExecutionLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobExecutionInstanceLock = new AtomicBoolean(false);
    protected final AtomicBoolean stepExecutionLock = new AtomicBoolean(false);
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
            jobInstanceExecutions.put(id, new ArrayList<JobExecution>(0));
        } finally {
            jobInstanceExecutionLock.set(false);
        }
        return instance;
    }

    @Override
    public JobExecution createJobExecution(final JobInstance instance) throws NoSuchJobInstanceException {
        final long id = jobExecutionIndex.incrementAndGet();
        final JobExecutionImpl execution = new JobExecutionImpl.Builder()
                .setExecutionId(id)
                .setJobName(instance.getJobName())
                .setBatchStatus(BatchStatus.STARTING)
                .setExitStatus(BatchStatus.STARTING.name())
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
            latestJobExecutionForInstance.put(instance.getInstanceId(), execution);
        } finally {
            latestJobExecutionForInstanceLock.set(false);
        }
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<JobExecution> executions = jobInstanceExecutions.get(instance.getInstanceId());
            if (executions == null) {
                throw new NoSuchJobInstanceException();
            }
            executions.add(execution);
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
                .setExitStatus(BatchStatus.STARTING.name())
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
            final JobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            jobExecutions.put(executionId, JobExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setStart(timestamp)
                    .setBatchStatus(BatchStatus.STARTED)
                    .setExitStatus(BatchStatus.STARTED.name())
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
            final JobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            jobExecutions.put(executionId, JobExecutionImpl.from(execution)
                    .setUpdated(timestamp)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(batchStatus.name())
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void finishJobExecution(final long executionId, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final JobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            jobExecutions.put(executionId, JobExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(exitStatus)
                    .setUpdated(timestamp)
                    .setEnd(timestamp)
                    .build()
            );
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long executionId, final Serializable serializable, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(executionId);
             if (execution == null) {
                 throw new NoSuchJobExecutionException();
             }
            stepExecutions.put(executionId, StepExecutionImpl.from(execution)
                    .setPersistentUserData(serializable)
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void updateStepExecution(final long executionId, final BatchStatus batchStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(executionId);
             if (execution == null) {
                 throw new NoSuchJobExecutionException();
             }
            stepExecutions.put(executionId, StepExecutionImpl.from(execution)
                    .setBatchStatus(batchStatus)
                    .setExitStatus(batchStatus.name())
                    .build()
            );
        } finally {
            stepExecutionLock.set(false);
        }
    }

    @Override
    public void finishStepExecution(final long executionId, final BatchStatus batchStatus, final String exitStatus, final Date timestamp) throws NoSuchJobExecutionException, JobSecurityException {
        while (!stepExecutionLock.compareAndSet(false, true)) {}
        try {
            final StepExecution execution = stepExecutions.get(executionId);
             if (execution == null) {
                 throw new NoSuchJobExecutionException();
             }
            stepExecutions.put(executionId, StepExecutionImpl.from(execution)
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
    public Set<String> getJobNames() throws JobSecurityException {
        final Set<String> ret = new THashSet<String>();
        while (!jobInstanceLock.compareAndSet(false, true)) {}
        try {
            for (final JobInstance instance : jobInstances.values()) {
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
            for (final JobInstance instance : jobInstances.values()) {
                if (jobName.equals(instance.getJobName())) {
                    ++ret;
                }
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
            for (final JobInstance instance : jobInstances.values()) {
                if (jobName.equals(instance.getJobName())) {
                    ret.add(instance);
                }
            }
        } finally {
            jobInstanceLock.set(false);
        }
        Collections.reverse(ret);
        return ret.subList(start, count);
    }

    @Override
    public List<Long> getRunningExecutions(final String jobName) throws NoSuchJobException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final List<Long> ids = new ArrayList<Long>();
            for (final JobExecution execution : jobExecutions.values()) {
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
    public JobInstance getJobInstance(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
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
        final List<JobExecution> executions;
        while (!jobInstanceExecutionLock.compareAndSet(false, true)) {}
        try {
            executions = jobInstanceExecutions.get(instance.getInstanceId());
            if (executions == null) {
                throw new NoSuchJobInstanceException();
            }
            return executions;
        } finally {
            jobInstanceExecutionLock.set(false);
        }
    }

    @Override
    public JobExecution getJobExecution(final long executionId) throws NoSuchJobExecutionException, JobSecurityException {
        while (!jobExecutionLock.compareAndSet(false, true)) {}
        try {
            final JobExecution execution = jobExecutions.get(executionId);
            if (execution == null) {
                throw new NoSuchJobExecutionException();
            }
            return execution;
        } finally {
            jobExecutionLock.set(false);
        }
    }

    @Override
    public JobExecution getLatestJobExecution(final long executionId) throws NoSuchJobExecutionException, JobExecutionNotMostRecentException, JobSecurityException {
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

        final JobExecution execution;
        while (!latestJobExecutionForInstanceLock.compareAndSet(false, true)) {}
        try {
            execution = latestJobExecutionForInstance.get(instance.getInstanceId());
            if (execution == null) {
                throw new JobExecutionNotMostRecentException();
            }
            return execution;
        } finally {
            latestJobExecutionForInstanceLock.set(false);
        }
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
