package io.machinecode.chainlink.repository.gridgain;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.repository.core.BaseMapExecutionRepository;
import io.machinecode.chainlink.spi.marshalling.Marshaller;
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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainExecutionRepository extends BaseMapExecutionRepository {

    public static final String IDS = GridGainExecutionRepository.class.getCanonicalName() + ".ids";
    public static final String JOB_INSTANCES = GridGainExecutionRepository.class.getCanonicalName() + ".jobInstances";
    public static final String JOB_EXECUTIONS = GridGainExecutionRepository.class.getCanonicalName() + ".jobExecutions";
    public static final String STEP_EXECUTIONS = GridGainExecutionRepository.class.getCanonicalName() + ".stepExecutions";
    public static final String PARTITION_EXECUTIONS = GridGainExecutionRepository.class.getCanonicalName() + ".partitionExecutions";
    public static final String JOB_INSTANCE_EXECUTIONS = GridGainExecutionRepository.class.getCanonicalName() + ".jobInstanceExecutions";
    public static final String JOB_EXECUTION_INSTANCES = GridGainExecutionRepository.class.getCanonicalName() + ".jobExecutionInstances";
    public static final String JOB_EXECUTION_STEP_EXECUTIONS = GridGainExecutionRepository.class.getCanonicalName() + ".jobExecutionStepExecutions";
    public static final String LATEST_JOB_EXECUTION_FOR_INSTANCE = GridGainExecutionRepository.class.getCanonicalName() + ".latestJobExecutionForInstance";
    public static final String STEP_EXECUTION_PARTITION_EXECUTIONS = GridGainExecutionRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions";
    public static final String JOB_EXECUTION_HISTORY = GridGainExecutionRepository.class.getCanonicalName() + ".jobExecutionHistory";

    protected final GridGainMap<String, Long> ids;
    protected final GridGainMap<Long, ExtendedJobInstance> jobInstances;
    protected final GridGainMap<Long, ExtendedJobExecution> jobExecutions;
    protected final GridGainMap<Long, ExtendedStepExecution> stepExecutions;
    protected final GridGainMap<Long, PartitionExecution> partitionExecutions;
    protected final GridGainMap<Long, CopyOnWriteArrayList<Long>> jobInstanceExecutions;
    protected final GridGainMap<Long, Long> jobExecutionInstances;
    protected final GridGainMap<Long, CopyOnWriteArraySet<Long>> jobExecutionStepExecutions;
    protected final GridGainMap<Long, Long> latestJobExecutionForInstance;
    protected final GridGainMap<Long, CopyOnWriteArrayList<Long>> stepExecutionPartitionExecutions;
    protected final GridGainMap<Long, CopyOnWriteArraySet<Long>> jobExecutionHistory;

    protected final Grid grid;
    protected final boolean transactional;

    public GridGainExecutionRepository(final Marshaller marshaller, final Grid grid) {
        super(marshaller);
        this.grid = grid;

        this.ids = GridGainMap.with(grid.<String, Long>cache(IDS));
        this.transactional = this.ids.cache.configuration().getAtomicityMode() == GridCacheAtomicityMode.TRANSACTIONAL;
        this.jobInstances = GridGainMap.with(grid.<Long, ExtendedJobInstance>cache(JOB_INSTANCES));
        this.jobExecutions = GridGainMap.with(grid.<Long,ExtendedJobExecution>cache(JOB_EXECUTIONS));
        this.stepExecutions = GridGainMap.with(grid.<Long, ExtendedStepExecution>cache(STEP_EXECUTIONS));
        this.partitionExecutions = GridGainMap.with(grid.<Long,PartitionExecution>cache(PARTITION_EXECUTIONS));
        this.jobInstanceExecutions = GridGainMap.with(grid.<Long, CopyOnWriteArrayList<Long>>cache(JOB_INSTANCE_EXECUTIONS));
        this.jobExecutionInstances = GridGainMap.with(grid.<Long,Long>cache(JOB_EXECUTION_INSTANCES));
        this.jobExecutionStepExecutions = GridGainMap.with(grid.<Long, CopyOnWriteArraySet<Long>>cache(JOB_EXECUTION_STEP_EXECUTIONS));
        this.latestJobExecutionForInstance = GridGainMap.with(grid.<Long,Long>cache(LATEST_JOB_EXECUTION_FOR_INSTANCE));
        this.stepExecutionPartitionExecutions = GridGainMap.with(grid.<Long, CopyOnWriteArrayList<Long>>cache(STEP_EXECUTION_PARTITION_EXECUTIONS));
        this.jobExecutionHistory = GridGainMap.with(grid.<Long,CopyOnWriteArraySet<Long>>cache(JOB_EXECUTION_HISTORY));
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
        final Set<String> ret = new THashSet<String>();
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
    protected Collection<JobInstance> fetchJobInstances(final String jobName) throws Exception {
        final GridCacheQueryFuture<Map.Entry<Long,ExtendedJobInstance>> results = this.jobInstances.cache.queries()
                .createSqlQuery(GridGainJobInstance.class, "jobName = ?")
                .execute(jobName);
        final Set<JobInstance> ret = new THashSet<JobInstance>();
        for (final Map.Entry<Long, ExtendedJobInstance> entry : results.get()) {
            ret.add(entry.getValue());
        }
        return ret;
    }

    @Override
    protected Collection<Long> fetchRunningJobExecutionIds(final String jobName) throws Exception {
        final GridCacheQueryFuture<List<?>> results = this.jobExecutions.cache.queries()
                .createSqlFieldsQuery("select jobExecutionId from GridGainJobExecution where jobName = ? and ( batchStatus = ? or batchStatus = ? )")
                .execute(jobName, BatchStatus.STARTED, BatchStatus.STARTING);
        final Set<Long> ret = new THashSet<Long>();
        for (final List<?> list : results.get()) {
            for (final Object value : list) {
                ret.add((Long)value);
            }
        }
        return ret;
    }

    @Override
    protected Collection<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception {
        final GridCacheQueryFuture<Map.Entry<Long,ExtendedJobExecution>> results = this.jobExecutions.cache.queries()
                .createSqlQuery(GridGainJobExecution.class, "jobInstanceId = ?")
                .execute(jobInstanceId);
        final List<JobExecution> ret = new ArrayList<JobExecution>();
        for (final Map.Entry<Long, ExtendedJobExecution> entry : results.get()) {
            ret.add(entry.getValue());
        }
        return ret;
    }

    @Override
    public boolean isLocal() {
        return true;
    }

    protected GridGainJobInstance.Builder newJobInstanceBuilder() {
        return new GridGainJobInstance.Builder();
    }

    protected GridGainJobExecution.Builder newJobExecutionBuilder() {
        return new GridGainJobExecution.Builder();
    }
}
