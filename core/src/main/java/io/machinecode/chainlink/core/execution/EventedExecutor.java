package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ThreadId;
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
    protected EventedWorker getCallbackWorker(final ThreadId threadId) {
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
    protected List<BaseWorker> getWorkers(final List<BaseWorker> workers, final int required) {
        final ArrayList<BaseWorker> ret = new ArrayList<BaseWorker>(required);
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
    protected EventedWorker createWorker() {
        return new EventedWorker();
    }

    class EventedWorker extends BaseWorker {

        @Override
        protected void addToThreadPool() {
            while (!EventedExecutor.this.threadsLock.compareAndSet(false, true)) {}
            try {
                EventedExecutor.this.activeThreads.put(threadId, this);
            } finally {
                EventedExecutor.this.threadsLock.set(false);
            }
        }

        @Override
        protected void removeFromThreadPool() {
            while (!EventedExecutor.this.threadsLock.compareAndSet(false, true)) {}
            try {
                EventedExecutor.this.activeThreads.remove(threadId);
                EventedExecutor.this.closedThreads.put(threadId, this);
            } finally {
                EventedExecutor.this.threadsLock.set(false);
            }
        }
    }
}
