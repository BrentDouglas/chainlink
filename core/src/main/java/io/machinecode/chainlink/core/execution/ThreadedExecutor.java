package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import io.machinecode.chainlink.spi.execution.Executable;
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
public class ThreadedExecutor extends BaseExecutor {

    private static final Logger log = Logger.getLogger(ThreadedExecutor.class);

    protected final AtomicInteger currentWorker = new AtomicInteger(0);
    protected final TMap<ThreadId, ThreadedWorker> activeThreads = new THashMap<ThreadId, ThreadedWorker>();
    protected final TMap<ThreadId, ThreadedWorker> blockedThreads = new THashMap<ThreadId, ThreadedWorker>();
    protected final TMap<ThreadId, ThreadedWorker> closedThreads = new THashMap<ThreadId, ThreadedWorker>();
    protected final AtomicBoolean threadsLock = new AtomicBoolean(false);

    public ThreadedExecutor(final Configuration configuration) {
        super(configuration);
    }

    @Override
    public ThreadedWorker getWorker(final ThreadId threadId) {
        while (!threadsLock.compareAndSet(false, true)) {}
        try {
            return activeThreads.get(threadId);
        } finally {
            threadsLock.set(false);
        }
    }

    @Override
    public ThreadedWorker getCallbackWorker(final ThreadId threadId) {
        while (!threadsLock.compareAndSet(false, true)) {}
        try {
            final ThreadedWorker worker = blockedThreads.get(threadId);
            if (worker != null) {
                return worker;
            }
            return closedThreads.get(threadId);
        } finally {
            threadsLock.set(false);
        }
    }

    @Override
    public Worker getWorker() {
        synchronized (workers) {
            if (currentWorker.get() >= workers.size()) {
                currentWorker.set(0);
            }
            final Worker worker = workers.get(currentWorker.getAndIncrement());
            while (!threadsLock.compareAndSet(false, true)) {}
            try {
                blockedThreads.put(worker.getThreadId(), activeThreads.remove(worker.getThreadId()));
            } finally {
                threadsLock.set(false);
            }
            return worker;
        }
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final ArrayList<Worker> ret = new ArrayList<Worker>(required);
        synchronized (this.workers) {
            for (int i = 0; i < required; ++i) {
                if (currentWorker.get() >= workers.size()) {
                    currentWorker.set(0);
                }
                final Worker worker = workers.get(currentWorker.getAndIncrement());
                ret.add(worker);
                while (!threadsLock.compareAndSet(false, true)) {}
                try {
                    blockedThreads.put(worker.getThreadId(), activeThreads.remove(worker.getThreadId()));
                } finally {
                    threadsLock.set(false);
                }
            }
        }
        return ret;
    }

    @Override
    public ThreadedWorker createWorker() {
        return new ThreadedWorker(this);
    }

    public static class ThreadedWorker extends BaseWorker<ThreadedExecutor> {
        private final Listener listener = new Listener() {
            @Override
            public void run(final Deferred<?> that) {
                while (!executor.threadsLock.compareAndSet(false, true)) {}
                try {
                    executor.activeThreads.put(ThreadedWorker.this.threadId, executor.blockedThreads.remove(ThreadedWorker.this.threadId));
                } finally {
                    executor.threadsLock.set(false);
                }
            }
        };

        protected ThreadedWorker(final ThreadedExecutor executor) {
            super(executor);
        }

        protected void preExecute(final Executable executable) {
            executable.always(listener);
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
