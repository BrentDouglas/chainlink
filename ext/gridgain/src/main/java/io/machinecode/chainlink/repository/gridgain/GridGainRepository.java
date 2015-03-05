package io.machinecode.chainlink.repository.gridgain;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.repository.BaseMapRepository;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import org.gridgain.grid.Grid;
import org.gridgain.grid.cache.GridCacheAtomicityMode;
import org.gridgain.grid.cache.GridCacheTx;
import org.gridgain.grid.cache.query.GridCacheQueryFuture;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainRepository extends BaseMapRepository {

    public static final String IDS = GridGainRepository.class.getCanonicalName() + ".ids";
    public static final String JOB_INSTANCES = GridGainRepository.class.getCanonicalName() + ".jobInstances";
    public static final String JOB_EXECUTIONS = GridGainRepository.class.getCanonicalName() + ".jobExecutions";
    public static final String STEP_EXECUTIONS = GridGainRepository.class.getCanonicalName() + ".stepExecutions";
    public static final String PARTITION_EXECUTIONS = GridGainRepository.class.getCanonicalName() + ".partitionExecutions";
    public static final String JOB_INSTANCE_EXECUTIONS = GridGainRepository.class.getCanonicalName() + ".jobInstanceExecutions";
    public static final String JOB_EXECUTION_INSTANCES = GridGainRepository.class.getCanonicalName() + ".jobExecutionInstances";
    public static final String JOB_EXECUTION_STEP_EXECUTIONS = GridGainRepository.class.getCanonicalName() + ".jobExecutionStepExecutions";
    public static final String LATEST_JOB_EXECUTION_FOR_INSTANCE = GridGainRepository.class.getCanonicalName() + ".latestJobExecutionForInstance";
    public static final String STEP_EXECUTION_PARTITION_EXECUTIONS = GridGainRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions";
    public static final String JOB_EXECUTION_HISTORY = GridGainRepository.class.getCanonicalName() + ".jobExecutionHistory";

    protected final GridGainMap<String, Long> ids;
    protected final GridGainMap<Long, ExtendedJobInstance> jobInstances;
    protected final GridGainMap<Long, ExtendedJobExecution> jobExecutions;
    protected final GridGainMap<Long, ExtendedStepExecution> stepExecutions;
    protected final GridGainMap<Long, PartitionExecution> partitionExecutions;
    protected final GridGainMap<Long, List<Long>> jobInstanceExecutions;
    protected final GridGainMap<Long, Long> jobExecutionInstances;
    protected final GridGainMap<Long, Set<Long>> jobExecutionStepExecutions;
    protected final GridGainMap<Long, Long> latestJobExecutionForInstance;
    protected final GridGainMap<Long, List<Long>> stepExecutionPartitionExecutions;
    protected final GridGainMap<Long, Set<Long>> jobExecutionHistory;

    protected final Grid grid;
    protected final boolean transactional;

    public GridGainRepository(final Marshalling marshalling, final Grid grid) {
        super(marshalling);
        this.grid = grid;

        this.ids = GridGainMap.with(grid.<String, Long>cache(IDS));
        this.transactional = this.ids.cache.configuration().getAtomicityMode() == GridCacheAtomicityMode.TRANSACTIONAL;
        this.jobInstances = GridGainMap.with(grid.<Long, ExtendedJobInstance>cache(JOB_INSTANCES));
        this.jobExecutions = GridGainMap.with(grid.<Long,ExtendedJobExecution>cache(JOB_EXECUTIONS));
        this.stepExecutions = GridGainMap.with(grid.<Long, ExtendedStepExecution>cache(STEP_EXECUTIONS));
        this.partitionExecutions = GridGainMap.with(grid.<Long,PartitionExecution>cache(PARTITION_EXECUTIONS));
        this.jobInstanceExecutions = GridGainMap.with(grid.<Long, List<Long>>cache(JOB_INSTANCE_EXECUTIONS));
        this.jobExecutionInstances = GridGainMap.with(grid.<Long,Long>cache(JOB_EXECUTION_INSTANCES));
        this.jobExecutionStepExecutions = GridGainMap.with(grid.<Long, Set<Long>>cache(JOB_EXECUTION_STEP_EXECUTIONS));
        this.latestJobExecutionForInstance = GridGainMap.with(grid.<Long,Long>cache(LATEST_JOB_EXECUTION_FOR_INSTANCE));
        this.stepExecutionPartitionExecutions = GridGainMap.with(grid.<Long, List<Long>>cache(STEP_EXECUTION_PARTITION_EXECUTIONS));
        this.jobExecutionHistory = GridGainMap.with(grid.<Long,Set<Long>>cache(JOB_EXECUTION_HISTORY));
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
    protected Map<Long, List<Long>> jobInstanceExecutions() {
        return this.jobInstanceExecutions;
    }

    @Override
    protected Map<Long, Long> jobExecutionInstances() {
        return this.jobExecutionInstances;
    }

    @Override
    protected Map<Long, Set<Long>> jobExecutionStepExecutions() {
        return this.jobExecutionStepExecutions;
    }

    @Override
    protected Map<Long, Long> latestJobExecutionForInstance() {
        return this.latestJobExecutionForInstance;
    }

    @Override
    protected Map<Long, List<Long>> stepExecutionPartitionExecutions() {
        return this.stepExecutionPartitionExecutions;
    }

    @Override
    protected Map<Long, Set<Long>> jobExecutionHistory() {
        return this.jobExecutionHistory;
    }

    @Override
    protected long _id(final String key) throws Exception {
        if (!this.transactional) {
            final long id;
            final Long that = ids.get(key);
            id = that == null ? 1 : that + 1;
            ids.put(key, id);
            return id;
        }
        final GridCacheTx tx = ids.cache.txStart();
        try {
            final long id;
            final Long that = ids.get(key);
            id = that == null ? 1 : that + 1;
            ids.put(key, id);
            tx.commit();
            return id;
        } finally {
            tx.rollback();
        }
    }

    @Override
    protected Set<String> fetchJobNames() throws Exception {
        final GridCacheQueryFuture<List<?>> result = this.jobInstances.cache.queries()
                .createSqlFieldsQuery("select jobName from GridGainJobInstance")
                .execute();
        final Set<String> ret = new THashSet<>();
        for (final List<?> names : result.get()) {
            for (final Object name : names) {
                ret.add((String)name);
            }
        }
        return ret;
    }

    @Override
    protected int fetchJobInstanceCount(final String jobName) throws Exception {
        final GridCacheQueryFuture<Integer> results = this.jobInstances.cache.queries()
                .createSqlQuery(GridGainJobInstance.class, "jobName = ?")
                .execute(new GridGainCountReducer(), jobName);
        int count = 0;
        for (final Integer result : results.get()) {
           count += result;
        }
        return count;
    }

    @Override
    protected List<JobInstance> fetchJobInstances(final String jobName) throws Exception {
        final GridCacheQueryFuture<Map.Entry<Long,ExtendedJobInstance>> results = this.jobInstances.cache.queries()
                .createSqlQuery(GridGainJobInstance.class, "jobName = ?")
                .execute(jobName);
        final Collection<Map.Entry<Long,ExtendedJobInstance>> values = results.get();
        final List<JobInstance> ret = new ArrayList<>(values.size());
        for (final Map.Entry<Long, ExtendedJobInstance> entry : values) {
            ret.add(entry.getValue());
        }
        return ret;
    }

    @Override
    protected List<Long> fetchRunningJobExecutionIds(final String jobName) throws Exception {
        final GridCacheQueryFuture<List<?>> results = this.jobExecutions.cache.queries()
                .createSqlFieldsQuery("select jobExecutionId from GridGainJobExecution where jobName = ? and batchStatus = ?")
                .execute(jobName, BatchStatus.STARTED);
        final Collection<List<?>> values = results.get();
        final List<Long> ret = new ArrayList<>(values.size());
        for (final List<?> list : values) {
            for (final Object value : list) {
                ret.add((Long)value);
            }
        }
        return ret;
    }

    @Override
    protected List<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception {
        final GridCacheQueryFuture<Map.Entry<Long,ExtendedJobExecution>> results = this.jobExecutions.cache.queries()
                .createSqlQuery(GridGainJobExecution.class, "jobInstanceId = ?")
                .execute(jobInstanceId);
        final List<JobExecution> ret = new ArrayList<>();
        for (final Map.Entry<Long, ExtendedJobExecution> entry : results.get()) {
            ret.add(entry.getValue());
        }
        return ret;
    }

    protected GridGainJobInstance.Builder newJobInstanceBuilder() {
        return new GridGainJobInstance.Builder();
    }

    protected GridGainJobExecution.Builder newJobExecutionBuilder() {
        return new GridGainJobExecution.Builder();
    }
}
