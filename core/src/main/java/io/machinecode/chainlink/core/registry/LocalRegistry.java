package io.machinecode.chainlink.core.registry;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.JobRegistry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.On;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalRegistry implements Registry {

    private static final Logger log = Logger.getLogger(LocalRegistry.class);

    protected final List<Worker> workerOrder;
    protected final TMap<WorkerId, Worker> workers;
    protected final TMap<ExecutionRepositoryId, ExecutionRepository> repositories;

    protected final AtomicInteger currentWorker = new AtomicInteger(0);

    protected final AtomicBoolean workerLock = new AtomicBoolean(false);
    protected final AtomicBoolean repositoryLock = new AtomicBoolean(false);

    protected final TLongObjectMap<JobRegistry> jobRegistries = new TLongObjectHashMap<JobRegistry>();
    protected final TLongObjectMap<Chain<?>> jobs = new TLongObjectHashMap<Chain<?>>();
    protected final List<On<Long>> onRegisters = new ArrayList<On<Long>>();
    protected final List<On<Long>> onUnregisters = new ArrayList<On<Long>>();
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);

    public LocalRegistry() {
        this.workerOrder = new ArrayList<Worker>();
        this.workers = new THashMap<WorkerId, Worker>();
        this.repositories = new THashMap<ExecutionRepositoryId, ExecutionRepository>();
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
    public ChainId generateChainId() {
        return new UUIDId();
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new UUIDId();
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new ThreadId((Thread)worker);
        } else {
            return new UUIDId();
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new UUIDId();
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
                if (id.equals(worker.id())) {
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
    public ChainId registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) {
        final JobRegistry jobRegistry = _createJobRegistry(jobExecutionId);
        final ChainId id = jobRegistry.registerChain(chainId, chain);
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobs.put(jobExecutionId, chain);
            this.jobRegistries.put(jobExecutionId, jobRegistry);
            for (final On<Long> on : onRegisters) {
                on.on(jobExecutionId);
            }
            log.debugf(Messages.get("CHAINLINK-005100.registry.put.job"), jobExecutionId);
        } finally {
            jobLock.set(false);
        }
        return id;
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
    public JobRegistry getJobRegistry(final long jobExecutionId) throws JobExecutionNotRunningException {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final JobRegistry job = this.jobRegistries.get(jobExecutionId);
            if (job == null) {
                throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.registry.no.job", jobExecutionId));
            }
            return job;
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public void unregisterJob(final long jobExecutionId) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final Chain<?> job = this.jobs.remove(jobExecutionId);
            this.jobRegistries.remove(jobExecutionId);
            if (job != null) {
                for (final On<Long> on : onUnregisters) {
                    on.on(jobExecutionId);
                }
            }
        } finally {
            jobLock.set(false);
        }
        log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
    }

    @Override
    public void onRegisterJob(final On<Long> on) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.onRegisters.add(on);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public void onUnregisterJob(final On<Long> on) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.onUnregisters.add(on);
        } finally {
            jobLock.set(false);
        }
    }

    protected JobRegistry _createJobRegistry(final long jobExecutionId) {
        return new LocalJobRegistry();
    }
}
