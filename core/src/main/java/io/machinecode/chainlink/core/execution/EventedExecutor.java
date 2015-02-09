package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.transport.WorkerState;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.transport.Transport;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
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
        if (exception != null) {
            throw exception;
        }
    }

    @Override
    public Worker getWorker() {
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
