package io.machinecode.nock.core.exec;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.core.deferred.AllDeferredImpl;
import io.machinecode.nock.core.inject.InjectionContextImpl;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.ExecutableEvent;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Worker;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutor implements Executor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final ExecutionRepository repository;
    protected final TransactionManager transactionManager;
    protected final InjectionContext injectionContext;

    protected final List<WorkerImpl> workers;
    protected final TMap<ThreadId, Worker> threads = new THashMap<ThreadId, Worker>();
    protected final AtomicBoolean threadsLock = new AtomicBoolean(false);

    protected final TLongObjectMap<Deferred<?>> jobs = new TLongObjectHashMap<Deferred<?>>();
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);

    public EventedExecutor(final RuntimeConfiguration configuration, final int threads) {
        this.transactionManager = configuration.getTransactionManager();
        this.repository = configuration.getRepository();
        this.injectionContext = new InjectionContextImpl(configuration);
        this.workers = new ArrayList<WorkerImpl>(threads);
        for (int i = 0; i < threads; ++i) {
            final WorkerImpl worker = new WorkerImpl();
            this.workers.add(worker);
            worker.start();
            worker.addToThreadPool();
        }
    }

    @Override
    public Deferred<?> getJob(final long executionId) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            return this.jobs.get(executionId);
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public Deferred<?> removeJob(final long jobExecutionId) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            final Deferred<?> job = this.jobs.remove(jobExecutionId);
            synchronized (job) {
                job.notifyAll();
            }
            return job;
        } finally {
            jobLock.set(false);
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    @Override
    public ExecutionRepository getRepository() {
        return repository;
    }

    @Override
    public Deferred<?> execute(final Executable executable) {
        final Worker worker = _leastBusy(workers, Collections.<Worker>emptySet());
        worker.addExecutable(new ExecutableEventImpl<Executable>(executable, null));
        return executable;
    }

    @Override
    public Deferred<?> callback(final CallbackExecutable executable, final ExecutionContext context) {
        final Worker worker = _leastBusy(workers, Collections.<Worker>emptySet());
        worker.addCallback(new ExecutableEventImpl<CallbackExecutable>(executable, null, context));
        return executable;
    }

    @Override
    public Deferred<?> execute(final ThreadId threadId, final Executable executable) {
        final Worker worker = getWorker(threadId);
        worker.addExecutable(new ExecutableEventImpl<Executable>(executable, null));
        return executable;
    }

    @Override
    public Deferred<?> callback(final ThreadId threadId, final CallbackExecutable executable, final ExecutionContext context) {
        final Worker worker = getWorker(threadId);
        worker.addCallback(new ExecutableEventImpl<CallbackExecutable>(executable, null, context));
        return executable;
    }

    @Override
    public Deferred<?> execute(final int maxThreads, final Executable... executables) {
        final Set<Worker> used = new THashSet<Worker>();
        for (int i = 0; i < maxThreads; ++i) {
            used.add(_leastBusy(workers, used));
        }
        Iterator<Worker> it = used.iterator();
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = used.iterator();
            }
            final Worker worker = it.next();
            worker.addExecutable(new ExecutableEventImpl<Executable>(executable, null));
        }
        return new AllDeferredImpl<Executable, Throwable>(executables);
    }

    @Override
    public Worker getWorker(final ThreadId threadId) {
        while (!threadsLock.compareAndSet(false, true)) {}
        try {
            return threads.get(threadId);
        } finally {
            threadsLock.set(false);
        }
    }

    @Override
    public InjectionContext createInjectionContext(final ExecutionContext context) {
        return injectionContext;
    }

    private void _stringifyThreads(final StringBuilder builder) {
        while (!threadsLock.compareAndSet(false, true)) {}
        try {
            for (final ThreadId that : threads.keySet()) {
                builder.append(that.toString()).append('\n');
            }
        } finally {
            threadsLock.set(false);
        }
    }

    private WorkerImpl _leastBusy(final Collection<WorkerImpl> workers, final Set<Worker> exclude) {
        int leastQueueSize;
        WorkerImpl leastBusy = null;
        synchronized (workers) {
            leastQueueSize = workers.size();
            for (final WorkerImpl worker : workers) {
                final int queueSize;
                synchronized (worker.executables) {
                    queueSize = worker.executables.size();
                }
                if ((queueSize < leastQueueSize || leastBusy == null) && !exclude.contains(worker)) {
                    leastBusy = worker;
                    leastQueueSize = queueSize;
                }
            }
        }
        return leastBusy;
    }

    private static enum LastRan { EXECUTABLE, CALLBACK }

    class WorkerImpl extends Thread implements Worker {
        private final ThreadIdImpl threadId = new ThreadIdImpl(this);
        private volatile boolean running = true;
        private final Queue<ExecutableEvent<Executable>> executables = new LinkedList<ExecutableEvent<Executable>>();
        private final Queue<ExecutableEvent<CallbackExecutable>> callbacks = new LinkedList<ExecutableEvent<CallbackExecutable>>();
        private final Object lock = new Object();

        @Override
        public ThreadId getThreadId() {
            return threadId;
        }

        //These are in finally blocks as the jobs will never get cleared otherwise
        //TODO If build throws should not continue execution
        @Override
        public void addExecutable(final ExecutableEvent<Executable> event) {
            synchronized (lock) {
                executables.add(event);
                lock.notifyAll();
            }
        }

        @Override
        public void addCallback(final ExecutableEvent<CallbackExecutable> event) {
            synchronized (lock) {
                callbacks.add(event);
                lock.notifyAll();
            }
        }

        @Override
        public void run() {
            LastRan lastRan = LastRan.CALLBACK;
            while (running) {
                boolean ran;
                switch (lastRan) {
                    case CALLBACK:
                        ran = runExecutable();
                        ran = runCallback() || ran;
                        if (!ran) {
                            await();
                        }
                        lastRan = LastRan.EXECUTABLE;
                        break;
                    case EXECUTABLE:
                        ran = runCallback();
                        ran = runExecutable() || ran;
                        if (!ran) {
                            await();
                        }
                        lastRan = LastRan.CALLBACK;
                        break;
                    default:
                        throw new IllegalStateException("was " + lastRan); //TODO Messages
                }
            }
            removeFromThreadPool();
        }

        boolean runExecutable() {
            final ExecutableEvent<Executable> executableEvent = _nextFromQueue(executables);
            try {
                if (executableEvent == null) {
                    return false;
                }
                final Executable executable = executableEvent.getExecutable();
                executable.execute(EventedExecutor.this, threadId, executableEvent.getParentExecutable(), executableEvent.getContexts());
                return true;
            } catch (final Throwable e) {
                log.error("", e); //TODO Messages
                return false;
            } finally {
                synchronized (lock) {
                    executables.remove(executableEvent);
                }
            }
        }

        boolean runCallback() {
            final ExecutableEvent<CallbackExecutable> callbackEvent = _nextFromQueue(callbacks);
            try {
                if (callbackEvent == null) {
                    return false;
                }
                final CallbackExecutable callback = callbackEvent.getExecutable();
                callback.callback(EventedExecutor.this, threadId, callbackEvent.getParentExecutable(), callbackEvent.getContexts()[0]);
                return true;
            } catch (final Throwable e) {
                log.error("", e); //TODO Messages
                return false;
            } finally {
                synchronized (lock) {
                    callbacks.remove(callbackEvent);
                }
            }
        }

        private void await() {
            try {
                synchronized (lock) {
                    lock.wait();
                }
            } catch (final InterruptedException ie) {
                running = false;
                log.infof(Messages.format("worker.interrupted"));
                removeFromThreadPool();
                //TODO Log
            }
        }

        private <T extends Executable> ExecutableEvent<T> _nextFromQueue(final Queue<ExecutableEvent<T>> queue) {
            synchronized (lock) {
                try {
                    return queue.remove();
                } catch (final NoSuchElementException e) {
                    return null;
                }
            }
        }

        void addToThreadPool() {
            while (!EventedExecutor.this.threadsLock.compareAndSet(false, true)) {}
            try {
                EventedExecutor.this.threads.put(threadId, this);
            } finally {
                EventedExecutor.this.threadsLock.set(false);
            }
        }

        //TODO If this thread actually shuts down there is going to be some issues with running jobs referencing the dead thread
        void removeFromThreadPool() {
            while (!EventedExecutor.this.threadsLock.compareAndSet(false, true)) {}
            try {
                EventedExecutor.this.threads.remove(threadId);
            } finally {
                EventedExecutor.this.threadsLock.set(false);
            }
        }
    }
}
