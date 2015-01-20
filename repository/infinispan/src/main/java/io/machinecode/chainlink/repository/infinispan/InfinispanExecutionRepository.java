package io.machinecode.chainlink.repository.infinispan;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.repository.BaseMapExecutionRepository;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.repository.ExtendedJobExecution;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;
import io.machinecode.chainlink.spi.repository.ExtendedStepExecution;
import io.machinecode.chainlink.spi.repository.PartitionExecution;
import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.TransactionConfiguration;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.transaction.LockingMode;

import javax.batch.runtime.JobExecution;
import javax.batch.runtime.JobInstance;
import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanExecutionRepository extends BaseMapExecutionRepository {

    protected static final String IDS = InfinispanExecutionRepository.class.getCanonicalName() + ".ids";
    protected static final String JOB_INSTANCES = InfinispanExecutionRepository.class.getCanonicalName() + ".jobInstances";
    protected static final String JOB_EXECUTIONS = InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutions";
    protected static final String STEP_EXECUTIONS = InfinispanExecutionRepository.class.getCanonicalName() + ".stepExecutions";
    protected static final String PARTITION_EXECUTIONS = InfinispanExecutionRepository.class.getCanonicalName() + ".partitionExecutions";
    protected static final String JOB_INSTANCE_EXECUTIONS = InfinispanExecutionRepository.class.getCanonicalName() + ".jobInstanceExecutions";
    protected static final String JOB_EXECUTION_INSTANCES = InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutionInstances";
    protected static final String JOB_EXECUTION_STEP_EXECUTIONS = InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutionStepExecutions";
    protected static final String LATEST_JOB_EXECUTION_FOR_INSTANCE = InfinispanExecutionRepository.class.getCanonicalName() + ".latestJobExecutionForInstance";
    protected static final String STEP_EXECUTION_PARTITION_EXECUTIONS = InfinispanExecutionRepository.class.getCanonicalName() + ".stepExecutionPartitionExecutions";
    protected static final String JOB_EXECUTION_HISTORY = InfinispanExecutionRepository.class.getCanonicalName() + ".jobExecutionHistory";

    protected final AdvancedCache<String, Long> ids;
    protected final AdvancedCache<Long, ExtendedJobInstance> jobInstances;
    protected final AdvancedCache<Long, ExtendedJobExecution> jobExecutions;
    protected final AdvancedCache<Long, ExtendedStepExecution> stepExecutions;
    protected final AdvancedCache<Long, PartitionExecution> partitionExecutions;
    protected final AdvancedCache<Long, CopyOnWriteArrayList<Long>> jobInstanceExecutions;
    protected final AdvancedCache<Long, Long> jobExecutionInstances;
    protected final AdvancedCache<Long, CopyOnWriteArraySet<Long>> jobExecutionStepExecutions;
    protected final AdvancedCache<Long, Long> latestJobExecutionForInstance;
    protected final AdvancedCache<Long, CopyOnWriteArrayList<Long>> stepExecutionPartitionExecutions;
    protected final AdvancedCache<Long, CopyOnWriteArraySet<Long>> jobExecutionHistory;

    protected final DistributedExecutorService jobInstanceExecutor;
    protected final DistributedExecutorService jobExecutionExecutor;

    protected final EmbeddedCacheManager cacheManager;

    public InfinispanExecutionRepository(final Marshalling marshalling, final EmbeddedCacheManager cacheManager) throws InterruptedException {
        super(marshalling);
        this.cacheManager = cacheManager;

        this.ids = _cache(cacheManager, IDS);
        this.jobInstances = _cache(cacheManager, JOB_INSTANCES);
        this.jobExecutions = _cache(cacheManager, JOB_EXECUTIONS);
        this.stepExecutions = _cache(cacheManager, STEP_EXECUTIONS);
        this.partitionExecutions = _cache(cacheManager, PARTITION_EXECUTIONS);
        this.jobInstanceExecutions = _cache(cacheManager, JOB_INSTANCE_EXECUTIONS);
        this.jobExecutionInstances = _cache(cacheManager, JOB_EXECUTION_INSTANCES);
        this.jobExecutionStepExecutions = _cache(cacheManager, JOB_EXECUTION_STEP_EXECUTIONS);
        this.latestJobExecutionForInstance = _cache(cacheManager, LATEST_JOB_EXECUTION_FOR_INSTANCE);
        this.stepExecutionPartitionExecutions = _cache(cacheManager, STEP_EXECUTION_PARTITION_EXECUTIONS);
        this.jobExecutionHistory = _cache(cacheManager, JOB_EXECUTION_HISTORY);

        this.jobInstanceExecutor = new DefaultExecutorService(jobInstances);
        this.jobExecutionExecutor = new DefaultExecutorService(jobExecutions);
    }

    private static void assertConfig(final Cache<?,?> cache) {
        final TransactionConfiguration tr = cache.getCacheConfiguration().transaction();
        if (tr.lockingMode() != LockingMode.PESSIMISTIC
                || !tr.transactionMode().isTransactional()) {
            throw new IllegalStateException("Cache " + cache.getName() + " must be transactional with pessimistic locking."); //TODO Message
        }
    }

    private static <K,V> AdvancedCache<K,V> _cache(final EmbeddedCacheManager cacheManager, final String region) throws InterruptedException {
        final Cache<K, V> cache = cacheManager.getCache(region, true);
        if (!cacheManager.isRunning(region)) {
            cacheManager.startCaches(region);
        }
        while (!cacheManager.isRunning(region)) {
            Thread.sleep(100);
        }
        assertConfig(cache);
        return cache.getAdvancedCache();
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
        final TransactionManager transactionManager = ids.getTransactionManager();
        transactionManager.begin();
        try {
            final long id;
            ids.lock(key);
            final Long that = ids.get(key);
            id = that == null ? 1 : that + 1;
            ids.put(key, id);
            transactionManager.commit();
            return id;
        } catch (final Exception e) {
            transactionManager.rollback();
            throw e;
        }
    }

    @Override
    protected Set<String> fetchJobNames() throws Exception {
        final Set<String> ret = new THashSet<>();
        final List<Future<Set<String>>> futures = jobInstanceExecutor.submitEverywhere(new JobNameCallable());
        for (final Future<Set<String>> future : futures) {
            final Set<String> value = future.get();
            if (value != null) {
                ret.addAll(future.get());
            }
        }
        return ret;
    }

    @Override
    protected int fetchJobInstanceCount(final String jobName) throws Exception {
        int count = 0;
        final List<Future<Integer>> futures = jobInstanceExecutor.submitEverywhere(new JobInstanceCountCallable(jobName));
        for (final Future<Integer> future : futures) {
            final Integer value = future.get();
            if (value != null) {
                count += value;
            }
        }
        return count;
    }

    @Override
    protected List<JobInstance> fetchJobInstances(final String jobName) throws Exception {
        final List<JobInstance> ret = new ArrayList<>();
        final List<Future<List<JobInstance>>> futures = jobInstanceExecutor.submitEverywhere(new JobInstanceCallable(jobName));
        for (final Future<List<JobInstance>> future : futures) {
            final List<JobInstance> value = future.get();
            if (value != null) {
                ret.addAll(value);
            }
        }
        return ret;
    }

    @Override
    protected List<Long> fetchRunningJobExecutionIds(final String jobName) throws Exception {
        final List<Long> ret = new ArrayList<>();
        final List<Future<List<Long>>> futures = jobExecutionExecutor.submitEverywhere(new RunningJobExecutionIdCallable(jobName));
        for (final Future<List<Long>> future : futures) {
            final List<Long> value = future.get();
            if (value != null) {
                ret.addAll(value);
            }
        }
        return ret;
    }

    @Override
    protected List<JobExecution> fetchJobExecutionsForJobInstance(final long jobInstanceId) throws Exception {
        final List<JobExecution> ret = new ArrayList<>();
        final List<Future<List<JobExecution>>> futures = jobExecutionExecutor.submitEverywhere(new JobExecutionsForJobInstanceCallable(jobInstanceId));
        for (final Future<List<JobExecution>> future : futures) {
            final List<JobExecution> value = future.get();
            if (value != null) {
                ret.addAll(value);
            }
        }
        return ret;
    }
}
