package io.machinecode.chainlink.core.registry;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.SplitAccumulator;
import io.machinecode.chainlink.spi.registry.StepAccumulator;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.WhenDeferred;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LocalRegistry implements Registry {

    private static final Logger log = Logger.getLogger(LocalRegistry.class);

    protected final TMap<ExecutionRepositoryId, ExecutionRepository> repositories;

    protected final Lock repositoryLock = new ReentrantLock();

    protected final TLongObjectMap<LocalJobRegistry> jobRegistries = new TLongObjectHashMap<>();
    protected final TLongObjectMap<Chain<?>> jobs = new TLongObjectHashMap<>();
    protected final Lock jobLock = new ReentrantLock();

    protected final TLongObjectMap<TMap<Key, Object>> artifacts = new TLongObjectHashMap<>();
    protected final Lock artifactLock = new ReentrantLock();

    protected final TMap<String, JobEventListener> jobEvents = new THashMap<>();

    public LocalRegistry() {
        this.repositories = new THashMap<>();
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        // no op
    }

    @Override
    public void close() throws Exception {
        // no op
    }

    @Override
    public ExecutionRepositoryId registerExecutionRepository(final ExecutionRepositoryId id, final ExecutionRepository repository) {
        repositoryLock.lock();
        try {
            this.repositories.put(id, repository);
        } finally {
            repositoryLock.unlock();
        }
        return id;
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        repositoryLock.lock();
        try {
            return this.repositories.get(id);
        } finally {
            repositoryLock.unlock();
        }
    }

    @Override
    public ExecutionRepository unregisterExecutionRepository(final ExecutionRepositoryId id) {
        repositoryLock.lock();
        try {
            return this.repositories.remove(id);
        } finally {
            repositoryLock.unlock();
        }
    }

    @Override
    public Promise<?,?,?> registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) {
        final LocalJobRegistry jobRegistry = _createJobRegistry(jobExecutionId);
        jobRegistry.registerChain(chainId, chain);
        jobLock.lock();
        try {
            this.jobs.put(jobExecutionId, chain);
            this.jobRegistries.put(jobExecutionId, jobRegistry);
            this.artifacts.remove(jobExecutionId);
            log.debugf(Messages.get("CHAINLINK-005100.registry.put.job"), jobExecutionId);
            return this.onRegisterJob(jobExecutionId, chain);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public Chain<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        jobLock.lock();
        try {
            final Chain<?> job = this.jobs.get(jobExecutionId);
            if (job == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.registry.no.job", jobExecutionId));
            }
            return job;
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public Promise<?,?,?> unregisterJob(final long jobExecutionId) {
        jobLock.lock();
        try {
            final Chain<?> job = this.jobs.remove(jobExecutionId);
            return this.onUnregisterJob(jobExecutionId, job).onComplete(new OnComplete() {
                @Override
                public void complete(final int state) {
                    LocalRegistry.this.jobRegistries.remove(jobExecutionId);
                }
            });
        } finally {
            jobLock.unlock();
        }
    }

    protected Promise<?,?,?> onRegisterJob(final long jobExecutionId, final Chain<?> job) {
        final List<Promise<?,?,?>> promises = new ArrayList<>(jobEvents.size());
        for (final JobEventListener on : this.jobEvents.values()) {
            final Promise<?,?,?> promise = on.onRegister(jobExecutionId, job);
            if (promise != null) {
                promises.add(promise);
            }
        }
        return new WhenDeferred<>(promises);
    }

    protected Promise<?,?,?> onUnregisterJob(final long jobExecutionId, final Chain<?> job) {
        log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
        final List<Promise<?,?,?>> promises = new ArrayList<>(jobEvents.size());
        for (final JobEventListener on : this.jobEvents.values()) {
            final Promise<?,?,?> promise = on.onUnregister(jobExecutionId, job);
            if (promise != null) {
                promises.add(promise);
            }
        }
        return new WhenDeferred<>(promises);
    }

    @Override
    public void registerChain(final long jobExecutionId, final ChainId id, final Chain<?> chain) {
        log.debugf("[je=%s] Registering chain with chainId=%s, chain=%s", jobExecutionId, id, chain); //TODO Message
        jobLock.lock();
        try {
            LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                this.jobRegistries.put(jobExecutionId, job = _createJobRegistry(jobExecutionId));
            }
            job.registerChain(id, chain);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public Chain<?> getChain(final long jobExecutionId, final ChainId id) {
        jobLock.lock();
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                return null;
            }
            return job.getChain(id);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public void registerExecutable(final long jobExecutionId, final Executable executable) {
        final ExecutableId id = executable.getId();
        jobLock.lock();
        try {
            LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                this.jobRegistries.put(jobExecutionId, job = _createJobRegistry(jobExecutionId));
            }
            job.registerExecutable(id, executable);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId id) {
        jobLock.lock();
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                return null;
            }
            return job.getExecutable(id);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public void registerJobEventListener(final String key, final JobEventListener on) {
        jobLock.lock();
        try {
            this.jobEvents.put(key, on);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public void unregisterJobEventListener(final String key) {
        jobLock.lock();
        try {
            this.jobEvents.remove(key);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public StepAccumulator getStepAccumulator(final long jobExecutionId, final String id) {
        jobLock.lock();
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            return job.getStepAccumulator(id);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public SplitAccumulator getSplitAccumulator(final long jobExecutionId, final String id) {
        jobLock.lock();
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            return job.getSplitAccumulator(id);
        } finally {
            jobLock.unlock();
        }
    }

    @Override
    public <T> T loadArtifact(final Class<T> clazz, final String ref, final ExecutionContext context) {
        final long jobExecutionId = context.getJobExecutionId();
        artifactLock.lock();
        try {
            final TMap<Key, Object> artifacts = this.artifacts.get(jobExecutionId);
            if (artifacts == null) {
                return null;
            }
            return clazz.cast(artifacts.get(new Key(jobExecutionId, context.getStepExecutionId(), context.getPartitionExecutionId(), ref, clazz)));
        } finally {
            artifactLock.unlock();
        }
    }

    @Override
    public <T> void storeArtifact(final Class<T> clazz, final String ref, final ExecutionContext context, final T value) {
        final long jobExecutionId = context.getJobExecutionId();
        artifactLock.lock();
        try {
            TMap<Key, Object> artifacts = this.artifacts.get(jobExecutionId);
            if (artifacts == null) {
                artifacts = new THashMap<>();
                this.artifacts.put(jobExecutionId, artifacts);
            }
            artifacts.put(new Key(jobExecutionId, context.getStepExecutionId(), context.getPartitionExecutionId(), ref, clazz), value);
        } finally {
            artifactLock.unlock();
        }
    }

    public static Chain<?> assertChain(final Chain<?> chain, final long jobExecutionId, final ChainId id) {
        if (chain == null) {
            throw new IllegalStateException("No chain registered matching. je=" + jobExecutionId + ",chainId=" + id); //TODO Message
        }
        return chain;
    }

    public static ExecutionRepository assertExecutionRepository(final ExecutionRepository repository, final ExecutionRepositoryId id) {
        if (repository == null) {
            throw new IllegalStateException("No execution repository registered matching. executionRepositoryId=" + id); //TODO Message
        }
        return repository;
    }

    public static Executable assertExecutable(final Executable executable, final long jobExecutionId, final ExecutableId id) {
        if (executable == null) {
            throw new IllegalStateException("No executable registered matching. je=" + jobExecutionId + ",executableId=" + id); //TODO Message
        }
        return executable;
    }

    protected LocalJobRegistry _createJobRegistry(final long jobExecutionId) {
        return new LocalJobRegistry();
    }

    private static class Key {
        final long jobExecutionId;
        final Long stepExecutionId;
        final Long partitionExecutionId;
        final String ref;
        final Class<?> clazz;

        private Key(final long jobExecutionId, final Long stepExecutionId, final Long partitionExecutionId, final String ref, final Class<?> clazz) {
            this.jobExecutionId = jobExecutionId;
            this.stepExecutionId = stepExecutionId;
            this.partitionExecutionId = partitionExecutionId;
            this.ref = ref;
            this.clazz = clazz;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final Key key = (Key) o;

            if (jobExecutionId != key.jobExecutionId) return false;
            if (!clazz.equals(key.clazz)) return false;
            if (partitionExecutionId != null ? !partitionExecutionId.equals(key.partitionExecutionId) : key.partitionExecutionId != null)
                return false;
            if (!ref.equals(key.ref)) return false;
            if (stepExecutionId != null ? !stepExecutionId.equals(key.stepExecutionId) : key.stepExecutionId != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = (int) (jobExecutionId ^ (jobExecutionId >>> 32));
            result = 31 * result + (stepExecutionId != null ? stepExecutionId.hashCode() : 0);
            result = 31 * result + (partitionExecutionId != null ? partitionExecutionId.hashCode() : 0);
            result = 31 * result + ref.hashCode();
            result = 31 * result + clazz.hashCode();
            return result;
        }
    }
}
