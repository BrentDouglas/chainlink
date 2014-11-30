package io.machinecode.chainlink.repository.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.repository.core.BaseMapExecutionRepository;
import io.machinecode.chainlink.spi.marshalling.MarshallingProvider;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastExecutonRepository extends BaseMapExecutionRepository {

    protected static final String IDS = HazelcastExecutonRepository.class.getCanonicalName() + ".ids";
    protected static final String JOB_INSTANCES = HazelcastExecutonRepository.class.getCanonicalName() + ".jobInstances";
    protected static final String JOB_EXECUTIONS = HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutions";
    protected static final String STEP_EXECUTIONS = HazelcastExecutonRepository.class.getCanonicalName() + ".stepExecutions";
    protected static final String PARTITION_EXECUTIONS = HazelcastExecutonRepository.class.getCanonicalName() + ".partitionExecutions";
    protected static final String JOB_INSTANCE_EXECUTIONS = HazelcastExecutonRepository.class.getCanonicalName() + ".jobInstanceExecutions";
    protected static final String JOB_EXECUTION_INSTANCES = HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutionInstances";
    protected static final String JOB_EXECUTION_STEP_EXECUTIONS = HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutionStepExecutions";
    protected static final String LATEST_JOB_EXECUTION_FOR_INSTANCE = HazelcastExecutonRepository.class.getCanonicalName() + ".latestJobExecutionForInstance";
    protected static final String STEP_EXECUTION_PARTITION_EXECUTIONS = HazelcastExecutonRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions";
    protected static final String JOB_EXECUTION_HISTORY = HazelcastExecutonRepository.class.getCanonicalName() + ".jobExecutionHistory";

    protected final IMap<String, Long> ids;
    protected final IMap<Long, ExtendedJobInstance> jobInstances;
    protected final IMap<Long, ExtendedJobExecution> jobExecutions;
    protected final IMap<Long, ExtendedStepExecution> stepExecutions;
    protected final IMap<Long, PartitionExecution> partitionExecutions;
    protected final IMap<Long, CopyOnWriteArrayList<Long>> jobInstanceExecutions;
    protected final IMap<Long, Long> jobExecutionInstances;
    protected final IMap<Long, CopyOnWriteArraySet<Long>> jobExecutionStepExecutions;
    protected final IMap<Long, Long> latestJobExecutionForInstance;
    protected final IMap<Long, CopyOnWriteArrayList<Long>> stepExecutionPartitionExecutions;
    protected final IMap<Long, CopyOnWriteArraySet<Long>> jobExecutionHistory;

    final HazelcastInstance hazelcast;

    public HazelcastExecutonRepository(final MarshallingProvider provider, final HazelcastInstance hazelcast) {
        super(provider);
        this.hazelcast = hazelcast;

        this.ids = this.hazelcast.getMap(IDS);
        this.jobInstances = this.hazelcast.getMap(JOB_INSTANCES);
        this.jobExecutions = this.hazelcast.getMap(JOB_EXECUTIONS);
        this.stepExecutions = this.hazelcast.getMap(STEP_EXECUTIONS);
        this.partitionExecutions = this.hazelcast.getMap(PARTITION_EXECUTIONS);
        this.jobInstanceExecutions = this.hazelcast.getMap(JOB_INSTANCE_EXECUTIONS);
        this.jobExecutionInstances = this.hazelcast.getMap(JOB_EXECUTION_INSTANCES);
        this.jobExecutionStepExecutions = this.hazelcast.getMap(JOB_EXECUTION_STEP_EXECUTIONS);
        this.latestJobExecutionForInstance = this.hazelcast.getMap(LATEST_JOB_EXECUTION_FOR_INSTANCE);
        this.stepExecutionPartitionExecutions = this.hazelcast.getMap(STEP_EXECUTION_PARTITION_EXECUTIONS);
        this.jobExecutionHistory = this.hazelcast.getMap(JOB_EXECUTION_HISTORY);
    }

    @Override
    protected Map<String, Long> ids() {
        return this.ids;
    }

    @Override
    protected Map<Long, ExtendedJobInstance> jobInstances() {
        return this.jobInstances;
    }

    @Override
    protected Map<Long, ExtendedJobExecution> jobExecutions() {
        return this.jobExecutions;
    }

    @Override
    protected Map<Long, ExtendedStepExecution> stepExecutions() {
        return this.stepExecutions;
    }

    @Override
    protected Map<Long, PartitionExecution> partitionExecutions() {
        return this.partitionExecutions;
    }

    @Override
    protected Map<Long, CopyOnWriteArrayList<Long>> jobInstanceExecutions() {
        return this.jobInstanceExecutions;
    }

    @Override
    protected Map<Long, Long> jobExecutionInstances() {
        return this.jobExecutionInstances;
    }

    @Override
    protected Map<Long, CopyOnWriteArraySet<Long>> jobExecutionStepExecutions() {
        return this.jobExecutionStepExecutions;
    }

    @Override
    protected Map<Long, Long> latestJobExecutionForInstance() {
        return this.latestJobExecutionForInstance;
    }

    @Override
    protected Map<Long, CopyOnWriteArrayList<Long>> stepExecutionPartitionExecutions() {
        return this.stepExecutionPartitionExecutions;
    }

    @Override
    protected Map<Long, CopyOnWriteArraySet<Long>> jobExecutionHistory() {
        return this.jobExecutionHistory;
    }

    @Override
    protected long _id(final String key) throws Exception {
        return hazelcast.getAtomicLong(key)
                .incrementAndGet();
    }

    @Override
    protected Set<String> fetchJobNames() throws Exception {
        final Set<String> ret = new THashSet<String>();
        for (final Object value : jobInstances.executeOnEntries(new JobNameProcessor()).values()) {
            ret.add((String)value);
        }
        return ret;
    }

    @Override
    protected int fetchJobInstanceCount(final String jobName) {
        int count = 0;
        for (final Object value : jobInstances.executeOnEntries(new JobInstanceCountProcessor(jobName)).values()) {
            if (value != null) {
                count += (Integer)value;
            }
        }
        return count;
    }

    @Override
    protected Collection<JobInstance> fetchJobInstances(final String jobName) throws Exception {
        final List<JobInstance> ret = new ArrayList<JobInstance>();
        for (final Object value : jobInstances.executeOnEntries(new JobInstanceProcessor(jobName)).values()) {
            if (value != null) {
                ret.add((JobInstance)value);
            }
        }
        return ret;
    }

    @Override
    protected Collection<Long> fetchRunningJobExecutionIds(final String jobName) {
        final List<Long> ret = new ArrayList<Long>();
        for (final Object value : jobInstances.executeOnEntries(new RunningJobExecutionIdProcessor(jobName)).values()) {
            if (value != null) {
                ret.add((Long)value);
            }
        }
        return ret;
    }

    @Override
    protected Collection<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception {
        final List<JobExecution> ret = new ArrayList<JobExecution>();
        for (final Object value : jobInstances.executeOnEntries(new JobExecutionsForJobInstanceProcessor(jobInstanceId)).values()) {
            if (value != null) {
                ret.add((JobExecution)value);
            }
        }
        return ret;
    }
}
