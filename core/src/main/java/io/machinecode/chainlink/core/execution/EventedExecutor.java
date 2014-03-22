package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutor extends BaseExecutor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final AtomicInteger worker = new AtomicInteger(0);
    protected final TMap<ThreadId, EventedWorker> activeThreads = new THashMap<ThreadId, EventedWorker>();
    protected final TMap<ThreadId, EventedWorker> closedThreads = new THashMap<ThreadId, EventedWorker>();
    protected final AtomicBoolean threadsLock = new AtomicBoolean(false);

    public EventedExecutor(final Configuration configuration) {
        super(configuration);
    }

    @Override
    public EventedWorker getWorker(final ThreadId threadId) {
        while (!threadsLock.compareAndSet(false, true)) {}
        try {
            return activeThreads.get(threadId);
        } finally {
            threadsLock.set(false);
        }
    }

    @Override
    public EventedWorker getCallbackWorker(final ThreadId threadId) {
        final EventedWorker worker = getWorker(threadId);
        if (worker != null) {
            return worker;
        }
        while (!threadsLock.compareAndSet(false, true)) {}
        try {
            return closedThreads.get(threadId);
        } finally {
            threadsLock.set(false);
        }
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final ArrayList<Worker> ret = new ArrayList<Worker>(required);
        synchronized (workers) {
            for (int i = 0; i < required; ++i) {
                if (worker.get() >= workers.size()) {
                    worker.set(0);
                }
                ret.add(workers.get(worker.getAndIncrement()));
            }
        }
        return ret;
    }

    @Override
    public Worker getWorker() {
        synchronized (workers) {
            if (worker.get() >= workers.size()) {
                worker.set(0);
            }
            return workers.get(worker.getAndIncrement());
        }
    }

    @Override
    public EventedWorker createWorker() {
        return new EventedWorker(this);
    }

    public static class EventedWorker extends BaseWorker<EventedExecutor> {

        public EventedWorker(final EventedExecutor executor) {
            super(executor);
        }

        @Override
        protected void addToThreadPool() {
            while (!executor.threadsLock.compareAndSet(false, true)) {}
            try {
                executor.activeThreads.put(threadId, this);
            } finally {
                executor.threadsLock.set(false);
            }
        }

        @Override
        protected void removeFromThreadPool() {
            while (!executor.threadsLock.compareAndSet(false, true)) {}
            try {
                executor.activeThreads.remove(threadId);
                executor.closedThreads.put(threadId, this);
            } finally {
                executor.threadsLock.set(false);
            }
        }
    }
}
