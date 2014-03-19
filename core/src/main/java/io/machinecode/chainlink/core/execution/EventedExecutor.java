package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.deferred.AllDeferredImpl;
import io.machinecode.chainlink.core.deferred.Notify;
import io.machinecode.chainlink.core.inject.InjectionContextImpl;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EventedExecutor implements Executor {

    private static final Logger log = Logger.getLogger(EventedExecutor.class);

    protected final ExecutionRepository repository;
    protected final TransactionManager transactionManager;
    protected final InjectionContext injectionContext;

    protected final AtomicInteger worker = new AtomicInteger(0);
    protected final List<WorkerImpl> workers;
    protected final TMap<ThreadId, Worker> threads = new THashMap<ThreadId, Worker>();
    protected final AtomicBoolean threadsLock = new AtomicBoolean(false);

    protected final TLongObjectMap<Deferred<?>> jobs = new TLongObjectHashMap<Deferred<?>>();
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);

    private final ExecutorService cancellation = Executors.newCachedThreadPool();

    public EventedExecutor(final RuntimeConfiguration configuration, final int threads) {
        this.transactionManager = configuration.getTransactionManager();
        this.repository = configuration.getRepository();
        this.injectionContext = new InjectionContextImpl(configuration);
        this.workers = new ArrayList<WorkerImpl>(threads);
        synchronized (workers) {
            for (int i = 0; i < threads; ++i) {
                final WorkerImpl worker = new WorkerImpl();
                this.workers.add(worker);
                worker.start();
                worker.addToThreadPool();
            }
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
    public InjectionContext getInjectionContext() {
        return injectionContext;
    }

    @Override
    public Deferred<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        final Deferred<?> job;
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            job = this.jobs.get(jobExecutionId);
        } finally {
            jobLock.set(false);
        }
        if (job == null) {
            throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.executor.no.job", jobExecutionId));
        }
        return job;
    }

    @Override
    public Deferred<?> removeJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        final Deferred<?> job;
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            job = this.jobs.remove(jobExecutionId);
        } finally {
            jobLock.set(false);
        }
        if (job == null) {
            throw new JobExecutionNotRunningException(Messages.format("CHAINLINK-005000.executor.no.job", jobExecutionId));
        }
        log.debugf(Messages.get("CHAINLINK-005101.executor.removed.job"), jobExecutionId);
        return job;
    }

    @Override
    public Deferred<?> execute(final long jobExecutionId, final Executable executable) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobs.put(jobExecutionId, executable);
            log.debugf(Messages.get("CHAINLINK-005100.executor.put.job"), jobExecutionId);
        } finally {
            jobLock.set(false);
        }
        return execute(executable);
    }

    @Override
    public Deferred<?> execute(final Executable executable) {
        final ThreadId threadId = executable.getThreadId();
        final Worker worker;
        if (threadId == null) {
            worker = _leastBusy(workers, 1).get(0);
        } else {
            worker = getWorker(threadId);
        }
        worker.addExecutable(new ExecutableEventImpl(executable));
        return executable;
    }

    @Override
    public Deferred<?> distribute(final int maxThreads, final Executable... executables) {
        final List<WorkerImpl> workers = _leastBusy(this.workers, maxThreads);
        ListIterator<WorkerImpl> it = workers.listIterator();
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = workers.listIterator();
            }
            it.next().addExecutable(new ExecutableEventImpl(executable));
        }
        return new AllDeferredImpl<Executable>(executables);
    }

    @Override
    public Deferred<?> callback(final Executable executable, final ExecutionContext context) {
        final ThreadId threadId = executable.getThreadId();
        final Worker worker;
        if (threadId == null) {
            worker = _leastBusy(workers, 1).get(0);
        } else {
            worker = getWorker(threadId);
        }
        worker.addExecutable(new ExecutableEventImpl(executable, context));
        return executable;
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
    public Future<?> cancel(final Deferred<?> deferred) {
        return cancellation.submit(new Runnable() {
            @Override
            public void run() {
                deferred.cancel(true);
            }
        });
    }

    private List<WorkerImpl> _leastBusy(final List<WorkerImpl> workers, final int required) {
        final ArrayList<WorkerImpl> ret = new ArrayList<WorkerImpl>(required);
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

    class WorkerImpl extends Thread implements Worker {
        private final ThreadIdImpl threadId = new ThreadIdImpl(this);
        private final Object lock = new Object();
        private volatile boolean running = true;
        private final Queue<ExecutableEvent> executables = new LinkedList<ExecutableEvent>();
        private final Notify notify = new Notify(lock);

        @Override
        public ThreadId getThreadId() {
            return threadId;
        }

        @Override
        public void addExecutable(final ExecutableEvent event) {
            log.debugf(Messages.get("CHAINLINK-024005.worker.add.executable"), threadId, event.getExecutable());
            synchronized (lock) {
                executables.add(event);
                lock.notifyAll();
            }
        }

        @Override
        public void run() {
            while (running) {
                runExecutable();
                awaitIfEmpty();
            }
        }

        void runExecutable() {
            final ExecutableEvent workEvent = _nextFromQueue(executables);
            if (workEvent == null) {
                return;
            }
            final Executable executable = workEvent.getExecutable();
            try {
                executable.onResolve(notify);
                executable.onReject(notify);
                executable.execute(EventedExecutor.this, threadId, executable.getParent(), workEvent.getContext());
            } catch (final Throwable e) {
                log.errorf(e, Messages.get("CHAINLINK-024004.worker.execute.execution"), threadId, executable);
            }
        }

        private void awaitIfEmpty() {
            try {
                synchronized (lock) {
                    if (executables.isEmpty()) {
                        log.tracef(Messages.get("CHAINLINK-024002.worker.waiting"), threadId);
                        lock.wait();
                        log.tracef(Messages.get("CHAINLINK-024003.worker.awake"), threadId);
                    }
                }
            } catch (final InterruptedException ie) {
                running = false;
                log.infof(Messages.get("CHAINLINK-024001.worker.interrupted"), threadId);
                removeFromThreadPool();
            }
        }

        private ExecutableEvent _nextFromQueue(final Queue<ExecutableEvent> queue) {
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

        @Override
        public String toString() {
            return "WorkerImpl[threadId=" + threadId + ",running=" + running + ",queued=" + executables.size() + "]";
        }
    }
}
