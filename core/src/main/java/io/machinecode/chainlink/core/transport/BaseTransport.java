package io.machinecode.chainlink.core.transport;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.execution.EventedWorker;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.core.registry.ThreadId;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.spi.Constants;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class BaseTransport<A> implements Transport<A> {

    protected final AtomicBoolean workerLock = new AtomicBoolean(false);
    protected final List<Worker> workerOrder;
    protected final TMap<WorkerId, Worker> workers;

    protected final AtomicInteger currentWorker = new AtomicInteger(0);

    protected final Registry registry;

    protected final ExecutorService executor;

    public BaseTransport(final Dependencies dependencies, final Properties properties) {
        this.registry = dependencies.getRegistry();
        this.workerOrder = new ArrayList<>();
        this.workers = new THashMap<>();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        final int numThreads;
        try {
            numThreads = Integer.decode(configuration.getProperty(Constants.THREAD_POOL_SIZE, Constants.Defaults.THREAD_POOL_SIZE));
        } catch (final NumberFormatException e) {
            throw new RuntimeException(e); //TODO Message
        }
        for (int i = 0; i < numThreads; ++i) {
            final Worker worker;
            try {
                worker = new EventedWorker(configuration);
            } catch (final Exception e) {
                throw new RuntimeException(e); //TODO Message
            }
            worker.start();
            while (!workerLock.compareAndSet(false, true)) {}
            try {
                this.workerOrder.add(worker);
                this.workers.put(worker.id(), worker);
            } finally {
                workerLock.set(false);
            }
        }
    }

    @Override
    public void close() throws Exception {
        while (!workerLock.compareAndSet(false, true)) {}
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
            workerLock.set(false);
        }
    }

    @Override
    public <T> Future<T> invokeLocal(final Command<T, A> command, final A origin) throws Exception {
        final Transport<A> self = this;
        return executor.submit(new Callable<T>() {
            @Override
            public T call() throws Exception {
                try {
                    return command.perform(self, origin);
                } catch (final Throwable e) {
                    throw new Exception(e);
                }
            }
        });
    }

    @Override
    public <T> void invokeRemote(final A address, final Command<T, A> command, final Deferred<T, Throwable,?> promise, final long timeout, final TimeUnit unit) {
        try {
            promise.resolve(command.perform(this, getLocal()));
        } catch (final Throwable e) {
            promise.reject(e);
        }
    }

    @Override
    public A getLocal() {
        return null;
    }

    @Override
    public Registry getRegistry() {
        return registry;
    }

    @Override
    public Worker getWorker(final WorkerId workerId) {
        return getLocalWorker(workerId);
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        return getLocalWorkers(required);
    }

    @Override
    public Worker getWorker(final long jobExecutionId, final ExecutableId executableId) {
        return getLocalWorker(jobExecutionId, executableId);
    }

    @Override
    public Worker getWorker() {
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
    public WorkerId leastBusyWorker() {
        return getWorker().id();
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        return LocalRegistry.assertExecutionRepository(
                registry.getExecutionRepository(id),
                id
        );
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId executableId) {
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
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            return workers.get(workerId);
        } finally {
            workerLock.set(false);
        }
    }

    protected Worker getLocalWorker(final long jobExecutionId, final ExecutableId executableId) {
        final Executable executable = registry.getExecutable(jobExecutionId, executableId);
        if (executable == null) {
            return null;
        }
        return getLocalWorker(executable.getWorkerId());
    }

    protected Worker getLocalWorker() {
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

    protected List<Worker> getLocalWorkers(final int required) {
        final ArrayList<Worker> ret = new ArrayList<>(required);
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
}
