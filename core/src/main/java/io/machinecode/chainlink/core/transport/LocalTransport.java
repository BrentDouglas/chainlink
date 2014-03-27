package io.machinecode.chainlink.core.transport;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.deferred.LinkedDeferred;
import io.machinecode.chainlink.core.execution.UUIDDeferredId;
import io.machinecode.chainlink.core.execution.UUIDExecutableId;
import io.machinecode.chainlink.core.execution.UUIDExecutionRepositoryId;
import io.machinecode.chainlink.core.execution.ThreadWorkerId;
import io.machinecode.chainlink.core.execution.UUIDWorkerId;
import io.machinecode.chainlink.jsl.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.ExecutableId;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransport implements Transport {

    private static final Logger log = Logger.getLogger(LocalTransport.class);

    protected final List<Worker> workerOrder;
    protected final TMap<WorkerId, Worker> workers;
    protected final TMap<ExecutionRepositoryId, ExecutionRepository> repositories;
    protected final TLongObjectMap<TMap<DeferredId, Deferred<?>>> deferreds;
    protected final TLongObjectMap<TMap<ExecutableId, Executable>> executables;

    protected final AtomicInteger currentWorker = new AtomicInteger(0);

    protected final AtomicBoolean workerLock = new AtomicBoolean(false);
    protected final AtomicBoolean repositoryLock = new AtomicBoolean(false);
    protected final AtomicBoolean deferredLock = new AtomicBoolean(false);
    protected final AtomicBoolean executableLock = new AtomicBoolean(false);

    protected final TLongObjectMap<Deferred<?>> jobs = new TLongObjectHashMap<Deferred<?>>();
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);

    public LocalTransport() {
        this.workerOrder = new ArrayList<Worker>();
        this.workers = new THashMap<WorkerId, Worker>();
        this.repositories = new THashMap<ExecutionRepositoryId, ExecutionRepository>();
        this.deferreds = new TLongObjectHashMap<TMap<DeferredId, Deferred<?>>>();
        this.executables = new TLongObjectHashMap<TMap<ExecutableId, Executable>>();
    }

    @Override
    public void startup() {
        //
    }

    @Override
    public void shutdown() {
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            this.workerOrder.clear();
            for (final Worker worker : this.workers.values()) {
                worker.shutdown();
            }
            this.workers.clear();
        } finally {
            workerLock.set(false);
        }
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new ThreadWorkerId((Thread)worker);
        } else {
            return new UUIDWorkerId(UUID.randomUUID());
        }
    }

    @Override
    public ExecutableId generateExecutableId(final Executable executable) {
        return new UUIDExecutableId(UUID.randomUUID());
    }

    @Override
    public DeferredId generateDeferredId(final Deferred<?> deferred) {
        return new UUIDDeferredId(UUID.randomUUID());
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId(final ExecutionRepository repository) {
        return new UUIDExecutionRepositoryId(UUID.randomUUID());
    }

    @Override
    public void registerDeferred(final long jobExecutionId, final DeferredId id, final Deferred<?> deferred) {
        while (!deferredLock.compareAndSet(false, true)) {}
        try {
            TMap<DeferredId, Deferred<?>> job = this.deferreds.get(jobExecutionId);
            if (job == null) {
                job = new THashMap<DeferredId, Deferred<?>>();
                this.deferreds.put(jobExecutionId, job);
            }
            job.put(id, deferred);
        } finally {
            deferredLock.set(false);
        }
    }

    @Override
    public Deferred<?> getDeferred(final long jobExecutionId, final DeferredId id) {
        while (!deferredLock.compareAndSet(false, true)) {}
        try {
            final TMap<DeferredId, Deferred<?>> job = this.deferreds.get(jobExecutionId);
            if (job == null) {
                return null;
            }
            return job.get(id);
        } finally {
            deferredLock.set(false);
        }
    }

    @Override
    public Deferred<?> unregisterDeferred(final long jobExecutionId, final DeferredId id) {
        while (!deferredLock.compareAndSet(false, true)) {}
        try {
            final TMap<DeferredId, Deferred<?>> job = this.deferreds.get(jobExecutionId);
            if (job == null) {
                return null;
            }
            return job.remove(id);
        } finally {
            deferredLock.set(false);
        }
    }

    @Override
    public void registerExecutable(final long jobExecutionId, final ExecutableId id, final Executable executable) {
        while (!executableLock.compareAndSet(false, true)) {}
        try {
            TMap<ExecutableId, Executable> job = this.executables.get(jobExecutionId);
            if (job == null) {
                job = new THashMap<ExecutableId, Executable>();
                this.executables.put(jobExecutionId, job);
            }
            job.put(id, executable);
        } finally {
            executableLock.set(false);
        }
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId id) {
        while (!executableLock.compareAndSet(false, true)) {}
        try {
            TMap<ExecutableId, Executable> job = this.executables.get(jobExecutionId);
            if (job == null) {
                return null;
            }
            return job.get(id);
        } finally {
            executableLock.set(false);
        }
    }

    @Override
    public Executable unregisterExecutable(final long jobExecutionId, final ExecutableId id) {
        while (!executableLock.compareAndSet(false, true)) {}
        try {
            TMap<ExecutableId, Executable> job = this.executables.get(jobExecutionId);
            if (job == null) {
                return null;
            }
            return job.remove(id);
        } finally {
            executableLock.set(false);
        }
    }

    @Override
    public void registerExecutionRepository(final ExecutionRepositoryId id, final ExecutionRepository repository) {
        while (!repositoryLock.compareAndSet(false, true)) {}
        try {
            this.repositories.put(id, repository);
        } finally {
            repositoryLock.set(false);
        }
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
    public void registerWorker(final WorkerId id, final Worker worker) {
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            this.workerOrder.add(worker);
            this.workers.put(id, worker);
        } finally {
            workerLock.set(false);
        }
    }

    @Override
    public Worker getWorker(final WorkerId workerId) {
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            return workers.get(workerId);
        } finally {
            workerLock.set(false);
        }
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final ArrayList<Worker> ret = new ArrayList<Worker>(required);
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            for (int i = 0; i < required; ++i) {
                if (currentWorker.get() >= workers.size()) {
                    currentWorker.set(0);
                }
                ret.add(workerOrder.get(currentWorker.getAndIncrement()));
            }
        } finally {
            workerLock.set(false);
        }
        return ret;
    }

    @Override
    public Worker getWorker() {
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            if (currentWorker.get() >= workers.size()) {
                currentWorker.set(0);
            }
            return workerOrder.get(currentWorker.getAndIncrement());
        } finally {
            workerLock.set(false);
        }
    }

    @Override
    public Worker unregisterWorker(final WorkerId id) {
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            for (final ListIterator<Worker> it = workerOrder.listIterator(); it.hasNext();) {
                final Worker worker = it.next();
                if (id.equals(worker.getWorkerId())) {
                    it.remove();
                    break;
                }
            }
            return workers.remove(id);
        } finally {
            workerLock.set(false);
        }
    }

    @Override
    public void registerJob(final long jobExecutionId, final Deferred<?> deferred) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobs.put(jobExecutionId, deferred);
            log.debugf(Messages.get("CHAINLINK-005100.executor.put.job"), jobExecutionId);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public Deferred<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        final Deferred<?> job;
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            job = this.jobs.get(jobExecutionId);
        } finally {
            jobLock.set(false);
        }
        if (job == null) {
            throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.executor.no.job", jobExecutionId));
        }
        return job;
    }

    @Override
    public void unregisterJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        final Deferred<?> job;
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            job = this.jobs.remove(jobExecutionId);
        } finally {
            jobLock.set(false);
        }
        while (!deferredLock.compareAndSet(false, true)) {}
        try {
            this.deferreds.remove(jobExecutionId);
        } finally {
            deferredLock.set(false);
        }
        while (!executableLock.compareAndSet(false, true)) {}
        try {
            this.executables.remove(jobExecutionId);
        } finally {
            executableLock.set(false);
        }
        if (job == null) {
            throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.executor.no.job", jobExecutionId));
        }
        log.debugf(Messages.get("CHAINLINK-005101.executor.removed.job"), jobExecutionId);
    }
}
