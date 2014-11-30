package io.machinecode.chainlink.repository.coherence;

import com.tangosol.net.CacheFactory;
import com.tangosol.net.NamedCache;
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
public class CoherenceExecutonRepository extends BaseMapExecutionRepository {

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

    public CoherenceExecutonRepository(final MarshallingProvider provider) {
        super(provider);

        CacheFactory.ensureCluster();
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
    protected Set<String> fetchJobNames() throws Exception {
        final Set<String> ret = new THashSet<String>();
        for (final Object value : jobInstances.invokeAll(new AllEntriesFilter(), new JobNameProcessor()).values()) {
            if (value != null) {
                ret.add((String)value);
            }
        }
        return ret;
    }

    @Override
    protected int fetchJobInstanceCount(final String jobName) {
        int count = 0;
        for (final Object value : jobInstances.invokeAll(new AllEntriesFilter(), new JobInstanceCountProcessor(jobName)).values()) {
            if (value != null) {
                count += (Integer)value;
            }
        }
        return count;
    }

    @Override
    protected Collection<JobInstance> fetchJobInstances(final String jobName) throws Exception {
        final List<JobInstance> ret = new ArrayList<JobInstance>();
        for (final Object value : jobInstances.invokeAll(new AllEntriesFilter(), new JobInstanceProcessor(jobName)).values()) {
            if (value != null) {
                ret.add((JobInstance)value);
            }
        }
        return ret;
    }

    @Override
    protected Collection<Long> fetchRunningJobExecutionIds(final String jobName) {
        final List<Long> ret = new ArrayList<Long>();
        for (final Object value : jobExecutions.invokeAll(new AllEntriesFilter(), new RunningJobExecutionIdProcessor(jobName)).values()) {
            if (value != null) {
                ret.add((Long)value);
            }
        }
        return ret;
    }

    @Override
    protected Collection<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception {
        final List<JobExecution> ret = new ArrayList<JobExecution>();
        for (final Object value : jobExecutions.invokeAll(new AllEntriesFilter(), new JobExecutionsForJobInstanceProcessor(jobInstanceId)).values()) {
            if (value != null) {
                ret.add((JobExecution)value);
            }
        }
        return ret;
    }
}
