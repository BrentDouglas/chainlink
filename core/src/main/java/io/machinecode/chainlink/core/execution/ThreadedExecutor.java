package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import io.machinecode.chainlink.spi.execution.Executable;
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
    protected ThreadedWorker getCallbackWorker(final ThreadId threadId) {
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
    protected List<BaseWorker> getWorkers(final List<BaseWorker> workers, final int required) {
        final ArrayList<BaseWorker> ret = new ArrayList<BaseWorker>(required);
        synchronized (workers) {
            for (int i = 0; i < required; ++i) {
                if (currentWorker.get() >= workers.size()) {
                    currentWorker.set(0);
                }
                final BaseWorker worker = workers.get(currentWorker.getAndIncrement());
                ret.add(worker);
                while (!threadsLock.compareAndSet(false, true)) {}
                try {
                    blockedThreads.put(worker.threadId, activeThreads.remove(worker.threadId));
                } finally {
                    threadsLock.set(false);
                }
            }
        }
        return ret;
    }

    @Override
    protected ThreadedWorker createWorker() {
        return new ThreadedWorker();
    }

    class ThreadedWorker extends BaseWorker {
        private final Listener listener = new Listener() {
            @Override
            public void run(final Deferred<?> that) {
                while (!threadsLock.compareAndSet(false, true)) {}
                try {
                    activeThreads.put(ThreadedWorker.this.threadId, blockedThreads.remove(ThreadedWorker.this.threadId));
                } finally {
                    threadsLock.set(false);
                }
            }
        };

        protected void preExecute(final Executable executable) {
            executable.always(listener);
        }

        @Override
        protected void addToThreadPool() {
            while (!ThreadedExecutor.this.threadsLock.compareAndSet(false, true)) {}
            try {
                ThreadedExecutor.this.activeThreads.put(threadId, this);
            } finally {
                ThreadedExecutor.this.threadsLock.set(false);
            }
        }

        @Override
        protected void removeFromThreadPool() {
            while (!ThreadedExecutor.this.threadsLock.compareAndSet(false, true)) {}
            try {
                ThreadedExecutor.this.activeThreads.remove(threadId);
                ThreadedExecutor.this.closedThreads.put(threadId, this);
            } finally {
                ThreadedExecutor.this.threadsLock.set(false);
            }
        }
    }
}
