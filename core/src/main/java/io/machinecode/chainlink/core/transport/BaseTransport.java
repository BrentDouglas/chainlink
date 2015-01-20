package io.machinecode.chainlink.core.transport;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.registry.ThreadId;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.then.api.Deferred;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class BaseTransport<A> implements Transport<A> {

    protected final Lock workerLock = new ReentrantLock();
    protected final Condition workerCondition = workerLock.newCondition();
    protected final List<Worker> workerOrder;
    protected final TMap<WorkerId, Worker> workers;

    protected final AtomicInteger currentWorker = new AtomicInteger(0);

    private final Registry registry;

    protected final ExecutorService executor;

    public BaseTransport(final Dependencies dependencies, final Properties properties) {
        this.registry = dependencies.getRegistry();
        this.workerOrder = new ArrayList<>();
        this.workers = new THashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        // no op
    }

    @Override
    public void registerWorker(final Worker worker) {
        workerLock.lock();
        try {
            this.workerOrder.add(worker);
            this.workers.put(worker.id(), worker);
            workerCondition.signalAll();
        } finally {
            workerLock.unlock();
        }
    }

    @Override
    public void unregisterWorker(final Worker worker) {
        workerLock.lock();
        try {
            this.workerOrder.remove(worker);
            this.workers.remove(worker.id());
        } finally {
            workerLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        workerLock.lock();
        try {
            this.workerOrder.clear();
            Exception exception = null;
            for (final Worker worker : this.workers.values()) {
                try {
                    worker.close();
                } catch (final Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
            this.workers.clear();
            if (exception != null) {
                throw exception;
            }
        } finally {
            workerLock.unlock();
        }
    }

    public <T> Future<T> invokeLocal(final Command<T, A> command, final A origin) throws Exception {
        final Transport<A> self = this;
        return executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    return command.perform(self, registry, origin);
                } catch (final Throwable e) {
                    throw new Exception(e);
                }
            }
        });
    }

    @Override
    public <T> void invokeRemote(final A address, final Command<T, A> command, final Deferred<T, Throwable,?> promise, final long timeout, final TimeUnit unit) {
        try {
            promise.resolve(command.perform(this, registry, getAddress()));
        } catch (final Throwable e) {
            promise.reject(e);
        }
    }

    @Override
    public A getAddress() {
        return null;
    }

    public Registry getRegistry() {
        return registry;
    }

    @Override
    public Worker getWorker(final WorkerId workerId) throws Exception {
        return getLocalWorker(workerId);
    }

    @Override
    public List<Worker> getWorkers(final int required) throws Exception {
        return getLocalWorkers(required);
    }

    @Override
    public Worker getWorker(final long jobExecutionId, final ExecutableId executableId) throws Exception {
        return getLocalWorker(jobExecutionId, executableId);
    }

    @Override
    public Worker getWorker() throws Exception {
        return getLocalWorker();
    }

    @Override
    public boolean hasWorker(final WorkerId workerId) {
        return getLocalWorker(workerId) != null;
    }

    @Override
    public boolean hasWorker(final long jobExecutionId, final ExecutableId executableId) {
        return getLocalWorker(jobExecutionId, executableId) != null;
    }

    @Override
    public WorkerId leastBusyWorker() throws Exception {
        return getWorker().id();
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) throws Exception {
        return LocalRegistry.assertExecutionRepository(
                registry.getExecutionRepository(id),
                id
        );
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId executableId) throws Exception {
        return LocalRegistry.assertExecutable(
                registry.getExecutable(jobExecutionId, executableId),
                jobExecutionId,
                executableId
        );
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

    protected Worker getLocalWorker(final WorkerId workerId) {
        workerLock.lock();
        try {
            return workers.get(workerId);
        } finally {
            workerLock.unlock();
        }
    }

    protected Worker getLocalWorker(final long jobExecutionId, final ExecutableId executableId) {
        final Executable executable = registry.getExecutable(jobExecutionId, executableId);
        if (executable == null) {
            return null;
        }
        return getLocalWorker(executable.getWorkerId());
    }

    protected Worker getLocalWorker() throws InterruptedException {
        workerLock.lock();
        try {
            return _getActiveLocalWorker();
        } finally {
            workerLock.unlock();
        }
    }

    protected List<Worker> getLocalWorkers(final int required) throws InterruptedException {
        final ArrayList<Worker> ret = new ArrayList<>(required);
        workerLock.lock();
        try {
            for (int i = 0; i < required; ++i) {
                ret.add(_getActiveLocalWorker());
            }
        } finally {
            workerLock.unlock();
        }
        return ret;
    }

    private Worker _getActiveLocalWorker() throws InterruptedException {
        Worker worker;
        do {
            if (workerOrder.size() == 0) {
                workerCondition.await();
            }
            if (currentWorker.get() >= workerOrder.size()) {
                currentWorker.set(0);
            }
            worker = workerOrder.get(currentWorker.getAndIncrement());
        } while (!worker.isActive());
        return worker;
    }
}
