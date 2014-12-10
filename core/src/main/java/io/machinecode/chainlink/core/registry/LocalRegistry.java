package io.machinecode.chainlink.core.registry;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
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
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.AllDeferred;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LocalRegistry implements Registry {

    private static final Logger log = Logger.getLogger(LocalRegistry.class);

    protected final TMap<ExecutionRepositoryId, ExecutionRepository> repositories;

    protected final AtomicBoolean repositoryLock = new AtomicBoolean(false);

    protected final TLongObjectMap<LocalJobRegistry> jobRegistries = new TLongObjectHashMap<>();
    protected final TLongObjectMap<Chain<?>> jobs = new TLongObjectHashMap<>();
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);

    protected final TLongObjectMap<TMap<Key, Object>> artifacts = new TLongObjectHashMap<>();
    protected final AtomicBoolean artifactLock = new AtomicBoolean(false);

    protected final TMap<String, JobEventListener> jobEvents = new THashMap<>();

    public LocalRegistry() {
        this.repositories = new THashMap<>();
    }

    @Override
    public void close() throws Exception {
        // no op
    }

    @Override
    public ExecutionRepositoryId registerExecutionRepository(final ExecutionRepositoryId id, final ExecutionRepository repository) {
        while (!repositoryLock.compareAndSet(false, true)) {}
        try {
            this.repositories.put(id, repository);
        } finally {
            repositoryLock.set(false);
        }
        return id;
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        while (!repositoryLock.compareAndSet(false, true)) {}
        try {
            return this.repositories.get(id);
        } finally {
            repositoryLock.set(false);
        }
    }

    @Override
    public ExecutionRepository unregisterExecutionRepository(final ExecutionRepositoryId id) {
        while (!repositoryLock.compareAndSet(false, true)) {}
        try {
            return this.repositories.remove(id);
        } finally {
            repositoryLock.set(false);
        }
    }

    @Override
    public Promise<?,?,?> registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) {
        final LocalJobRegistry jobRegistry = _createJobRegistry(jobExecutionId);
        jobRegistry.registerChain(chainId, chain);
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobs.put(jobExecutionId, chain);
            this.jobRegistries.put(jobExecutionId, jobRegistry);
            this.artifacts.remove(jobExecutionId);
            log.debugf(Messages.get("CHAINLINK-005100.registry.put.job"), jobExecutionId);
            return this.onRegisterJob(jobExecutionId, chain);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public Chain<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final Chain<?> job = this.jobs.get(jobExecutionId);
            if (job == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.registry.no.job", jobExecutionId));
            }
            return job;
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public Promise<?,?,?> unregisterJob(final long jobExecutionId) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final Chain<?> job = this.jobs.remove(jobExecutionId);
            return this.onUnregisterJob(jobExecutionId, job).onComplete(new OnComplete() {
                @Override
                public void complete(final int state) {
                    LocalRegistry.this.jobRegistries.remove(jobExecutionId);
                }
            });
        } finally {
            jobLock.set(false);
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
        return new AllDeferred<Void, Void, Void>(promises);
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
        return new AllDeferred<Void, Void, Void>(promises);
    }

    @Override
    public void registerChain(final long jobExecutionId, final ChainId id, final Chain<?> chain) {
        log.debugf("[je=%s] Registering chain with chainId=%s, chain=%s", jobExecutionId, id, chain); //TODO Message
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                this.jobRegistries.put(jobExecutionId, job = _createJobRegistry(jobExecutionId));
            }
            job.registerChain(id, chain);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public Chain<?> getChain(final long jobExecutionId, final ChainId id) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                throw new IllegalStateException("No chain registered matching. je=" + jobExecutionId + ",chainId=" + id); //TODO Message
            }
            return job.getChain(id);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public void registerExecutable(final long jobExecutionId, final Executable executable) {
        final ExecutableId id = executable.getId();
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                this.jobRegistries.put(jobExecutionId, job = _createJobRegistry(jobExecutionId));
            }
            job.registerExecutable(id, executable);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId id) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                throw new IllegalStateException("No executable registered matching. je=" + jobExecutionId + ",executableId=" + id); //TODO Message
            }
            return job.getExecutable(id);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public StepAccumulator getStepAccumulator(final long jobExecutionId, final String id) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            return job.getStepAccumulator(id);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public SplitAccumulator getSplitAccumulator(final long jobExecutionId, final String id) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final LocalJobRegistry job = this.jobRegistries.get(jobExecutionId);
            return job.getSplitAccumulator(id);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public <T> T loadArtifact(final Class<T> clazz, final String ref, final ExecutionContext context) {
        final long jobExecutionId = context.getJobExecutionId();
        while (!artifactLock.compareAndSet(false, true)) {}
        try {
            final TMap<Key, Object> artifacts = this.artifacts.get(jobExecutionId);
            if (artifacts == null) {
                return null;
            }
            return clazz.cast(artifacts.get(new Key(jobExecutionId, context.getStepExecutionId(), context.getPartitionExecutionId(), ref, clazz)));
        } finally {
            artifactLock.set(false);
        }
    }

    @Override
    public <T> void storeArtifact(final Class<T> clazz, final String ref, final ExecutionContext context, final T value) {
        final long jobExecutionId = context.getJobExecutionId();
        while (!artifactLock.compareAndSet(false, true)) {}
        try {
            TMap<Key, Object> artifacts = this.artifacts.get(jobExecutionId);
            if (artifacts == null) {
                artifacts = new THashMap<>();
                this.artifacts.put(jobExecutionId, artifacts);
            }
            artifacts.put(new Key(jobExecutionId, context.getStepExecutionId(), context.getPartitionExecutionId(), ref, clazz), value);
        } finally {
            artifactLock.set(false);
        }
    }

    @Override
    public void registerJobEventListener(final String key, final JobEventListener on) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobEvents.put(key, on);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public void unregisterJobEventListener(final String key) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobEvents.remove(key);
        } finally {
            jobLock.set(false);
        }
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
