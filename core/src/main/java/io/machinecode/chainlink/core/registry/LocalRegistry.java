/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.registry;

import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
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
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.registry.SplitAccumulator;
import io.machinecode.chainlink.spi.registry.StepAccumulator;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.WhenDeferred;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class LocalRegistry implements Registry {

    private static final Logger log = Logger.getLogger(LocalRegistry.class);

    protected final ReadWriteLock jobLock = new ReentrantReadWriteLock();
    protected final TLongObjectMap<LocalJobRegistry> jobRegistries = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<LocalJobRegistry>());
    protected final TLongObjectMap<Chain<?>> jobs = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<Chain<?>>());

    protected final TLongObjectMap<TMap<Key, Object>> artifacts = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<TMap<Key, Object>>());

    protected final ConcurrentMap<RepositoryId, Repository> repositories = new ConcurrentHashMap<>();
    protected final ConcurrentMap<String, JobEventListener> jobEvents = new ConcurrentHashMap<>();

    @Override
    public void open(final Configuration configuration) throws Exception {
        // no op
    }

    @Override
    public void close() throws Exception {
        // no op
    }

    @Override
    public void registerRepository(final RepositoryId id, final Repository repository) {
        this.repositories.put(id, repository);
    }

    @Override
    public Repository getRepository(final RepositoryId id) {
        return this.repositories.get(id);
    }

    @Override
    public Repository unregisterRepository(final RepositoryId id) {
        return this.repositories.remove(id);
    }

    @Override
    public Promise<?,?,?> registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) {
        final LocalJobRegistry jobRegistry = _createJobRegistry(jobExecutionId);
        jobRegistry.registerChain(chainId, chain);
        jobLock.writeLock().lock();
        try {
            this.jobs.put(jobExecutionId, chain);
            this.jobRegistries.put(jobExecutionId, jobRegistry);
        } finally {
            jobLock.writeLock().unlock();
        }
        this.artifacts.remove(jobExecutionId);
        log.debugf(Messages.get("CHAINLINK-005100.registry.put.job"), jobExecutionId);
        return this.onRegisterJob(jobExecutionId, chain);
    }

    @Override
    public Chain<?> getJob(final long jobExecutionId) {
        jobLock.readLock().lock();
        try {
            return this.jobs.get(jobExecutionId);
        } finally {
            jobLock.readLock().unlock();
        }
    }

    @Override
    public Promise<?,?,?> unregisterJob(final long jobExecutionId) {
        final Chain<?> job;
        jobLock.writeLock().lock();
        try {
            job = this.jobs.remove(jobExecutionId);
        } finally {
            jobLock.writeLock().unlock();
        }
        return this.onUnregisterJob(jobExecutionId, job).onComplete(new OnComplete() {
            @Override
            public void complete(final int state) {
                jobLock.writeLock().lock();
                try {
                    LocalRegistry.this.jobRegistries.remove(jobExecutionId);
                } finally {
                    jobLock.writeLock().unlock();
                }
            }
        });
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
        final LocalJobRegistry job = _getOrCreateJobRegistry(jobExecutionId);
        job.registerChain(id, chain);
    }

    @Override
    public Chain<?> getChain(final long jobExecutionId, final ChainId id) {
        final LocalJobRegistry job = _getJobRegistry(jobExecutionId);
        if (job == null) {
            return null;
        }
        return job.getChain(id);
    }

    @Override
    public void registerExecutable(final long jobExecutionId, final Executable executable) {
        final ExecutableId id = executable.getId();
        final LocalJobRegistry job = _getOrCreateJobRegistry(jobExecutionId);
        job.registerExecutable(id, executable);
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId id) {
        final LocalJobRegistry job = _getJobRegistry(jobExecutionId);
        if (job == null) {
            return null;
        }
        return job.getExecutable(id);
    }

    @Override
    public void registerJobEventListener(final String key, final JobEventListener on) {
        this.jobEvents.put(key, on);
    }

    @Override
    public void unregisterJobEventListener(final String key) {
        this.jobEvents.remove(key);
    }

    @Override
    public StepAccumulator getStepAccumulator(final long jobExecutionId, final String id) {
        final LocalJobRegistry job = _getJobRegistry(jobExecutionId);
        return job.getStepAccumulator(id);
    }

    @Override
    public SplitAccumulator getSplitAccumulator(final long jobExecutionId, final String id) {
        final LocalJobRegistry job = _getJobRegistry(jobExecutionId);
        return job.getSplitAccumulator(id);
    }

    @Override
    public <T> T loadArtifact(final Class<T> clazz, final String ref, final ExecutionContext context) {
        final long jobExecutionId = context.getJobExecutionId();
        final TMap<Key, Object> artifacts = this.artifacts.get(jobExecutionId);
        if (artifacts == null) {
            return null;
        }
        return clazz.cast(artifacts.get(new Key(jobExecutionId, context.getStepExecutionId(), context.getPartitionExecutionId(), ref, clazz)));
    }

    @Override
    public <T> void storeArtifact(final Class<T> clazz, final String ref, final ExecutionContext context, final T value) {
        final long jobExecutionId = context.getJobExecutionId();
        TMap<Key, Object> artifacts = this.artifacts.get(jobExecutionId);
        if (artifacts == null) {
            artifacts = new THashMap<>();
            this.artifacts.put(jobExecutionId, artifacts);
        }
        artifacts.put(new Key(jobExecutionId, context.getStepExecutionId(), context.getPartitionExecutionId(), ref, clazz), value);
    }

    private LocalJobRegistry _getJobRegistry(final long jobExecutionId) {
        jobLock.readLock().lock();
        try {
            return this.jobRegistries.get(jobExecutionId);
        } finally {
            jobLock.readLock().unlock();
        }
    }

    private LocalJobRegistry _getOrCreateJobRegistry(final long jobExecutionId) {
        LocalJobRegistry job = _getJobRegistry(jobExecutionId);
        if (job == null) {
            jobLock.writeLock().lock();
            try {
                this.jobRegistries.put(jobExecutionId, job = _createJobRegistry(jobExecutionId));
            } finally {
                jobLock.writeLock().unlock();
            }
        }
        return job;
    }

    public static Chain<?> assertChain(final Chain<?> chain, final long jobExecutionId, final ChainId id) {
        if (chain == null) {
            throw new IllegalStateException("No chain registered matching. je=" + jobExecutionId + ",chainId=" + id); //TODO Message
        }
        return chain;
    }

    public static Repository assertRepository(final Repository repository, final RepositoryId id) {
        if (repository == null) {
            throw new IllegalStateException("No execution repository registered matching. repositoryId=" + id); //TODO Message
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
