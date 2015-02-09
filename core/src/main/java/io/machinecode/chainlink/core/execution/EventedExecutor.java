package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.registry.UUIDId;
import io.machinecode.chainlink.core.then.ChainImpl;
import io.machinecode.chainlink.core.transport.WorkerState;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Transport;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventedExecutor implements Executor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final Registry registry;
    protected final Transport transport;
    protected final ExecutorService cancellation;
    protected final ThreadFactory factory;
    protected final int threads;
    protected Configuration configuration;

    protected final Lock workerLock = new ReentrantLock();
    protected final TMap<WorkerId, EventedWorker> workers = new THashMap<>();

    public EventedExecutor(final Dependencies dependencies, final Properties properties, final ThreadFactory factory) {
        this.registry = dependencies.getRegistry();
        this.transport = dependencies.getTransport();
        this.threads = Integer.decode(properties.getProperty(Constants.THREAD_POOL_SIZE, Constants.Defaults.THREAD_POOL_SIZE));
        this.factory = factory;
        this.cancellation = Executors.newSingleThreadExecutor(factory);
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        this.configuration = configuration;
        for (int i = 0; i < this.threads; ++i) {
            final EventedWorker worker = new EventedWorker(configuration);
            this.workers.put(worker.getId(), worker);
            factory.newThread(worker).start();
        }
    }

    @Override
    public void close() throws Exception {
        Exception exception = null;
        for (final EventedWorker worker : workers.values()) {
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
        this.cancellation.shutdown();
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public Chain<?> execute(final long jobExecutionId, final Executable executable) throws Exception {
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = new UUIDId(transport);
        registry.registerJob(jobExecutionId, chainId, chain);
        _execute(executable, chainId);
        return chain;
    }

    @Override
    public Chain<?> execute(final Executable executable) throws Exception {
        final Chain<?> chain = new ChainImpl<Void>();
        final ChainId chainId = new UUIDId(transport);
        registry.registerChain(executable.getContext().getJobExecutionId(), chainId, chain);
        _execute(executable, chainId);
        return chain;
    }

    private void _execute(final Executable executable, final ChainId chainId) throws Exception {
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker;
        if (workerId == null) {
            worker = getWorker();
        } else {
            final Worker local = getWorker(workerId);
            if (local != null) {
                worker = local;
            } else {
                throw new Exception(); // TODO Message
            }
        }
        worker.execute(new ExecutableEventImpl(executable, chainId));
    }

    private Worker getWorker() {
        final TreeSet<EventedWorker> set = new TreeSet<>(WorkerState.COMPARATOR);
        workerLock.lock();
        try {
            set.addAll(workers.values());
        } finally {
            workerLock.unlock();
        }
        return set.first();
    }

    @Override
    public Chain<?> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception {
        final long jobExecutionId = context.getJobExecutionId();
        final Executable executable = configuration.getRegistry().getExecutable(jobExecutionId, executableId);
        if (executable == null) {
            throw new Exception("No worker found with jobExecutionId=" + jobExecutionId + " and executableId" + executableId);
        }
        final WorkerId workerId = executable.getWorkerId();
        final Worker worker = configuration.getExecutor().getWorker(workerId);
        if (worker == null) {
            throw new Exception("No worker found with workerId=" + workerId);
        }
        final UUIDId id = new UUIDId(configuration.getTransport());
        final Chain<?> chain = new ChainImpl<>();
        registry.registerChain(jobExecutionId, id, chain);
        worker.callback(new CallbackEventImpl(jobExecutionId, executableId, id, context));
        return chain;
    }

    @Override
    public Future<?> cancel(final Future<?> promise) {
        return cancellation.submit(new Runnable() {
            @Override
            public void run() {
                promise.cancel(true);
            }
        });
    }

    @Override
    public Worker getWorker(final WorkerId workerId) throws Exception {
        workerLock.lock();
        try {
            return workers.get(workerId);
        } finally {
            workerLock.unlock();
        }
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final TreeSet<EventedWorker> set = new TreeSet<>(WorkerState.COMPARATOR);
        workerLock.lock();
        try {
            set.addAll(workers.values());
        } finally {
            workerLock.unlock();
        }
        final List<Worker> ret = new ArrayList<>(required);
        EventedWorker state = set.first();
        for (int i = 0; i < required; ++i) {
            ret.add(state);
            state = set.higher(state);
            if (state == null) {
                state = set.first();
            }
        }
        return ret;
    }
}
