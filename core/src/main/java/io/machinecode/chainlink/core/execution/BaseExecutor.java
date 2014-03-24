package io.machinecode.chainlink.core.execution;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.deferred.AllDeferredImpl;
import io.machinecode.chainlink.core.deferred.Notify;
import io.machinecode.chainlink.core.inject.InjectionContextImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class BaseExecutor implements Executor {

    private static final Logger log = Logger.getLogger(BaseExecutor.class);

    protected final ExecutionRepository repository;
    protected final TransactionManager transactionManager;
    protected final InjectionContext injectionContext;

    protected final List<Worker> workers;

    protected final TLongObjectMap<Deferred<?>> jobs = new TLongObjectHashMap<Deferred<?>>();
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);

    private final ExecutorService cancellation = Executors.newCachedThreadPool();
    private volatile int numThreads;

    public BaseExecutor(final Configuration configuration) {
        this.transactionManager = configuration.getTransactionManager();
        this.repository = configuration.getRepository();
        this.injectionContext = new InjectionContextImpl(configuration);
        //TODO cleanup
        final int threads = Integer.parseInt(configuration.getProperty(Constants.EXECUTOR_THREAD_POOL_SIZE));
        this.workers = new ArrayList<Worker>(threads);
        this.numThreads = threads;
    }

    @Override
    public void start() {
        synchronized (workers) {
            for (int i = 0; i < numThreads; ++i) {
                final Worker worker = createWorker();
                this.workers.add(worker);
                worker.start();
            }
        }
    }

    @Override
    public void stop() {
        this.cancellation.shutdown();
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
            worker = getWorker();
        } else {
            worker = getWorker(threadId);
        }
        final ExecutableEvent.Type type = executable.willSpawnCallback()
                ? ExecutableEvent.Type.EXECUTABLE_WITH_CALLBACK
                : ExecutableEvent.Type.EXECUTABLE;
        worker.addExecutable(new ExecutableEventImpl(executable, type));
        return executable;
    }

    @Override
    public Deferred<?> distribute(final int maxThreads, final Executable... executables) {
        final List<Worker> workers = getWorkers(maxThreads);
        ListIterator<Worker> it = workers.listIterator();
        for (final Executable executable : executables) {
            if (!it.hasNext()) {
                it = workers.listIterator();
            }
            final Worker worker = it.next();
            final ExecutableEvent.Type type = executable.willSpawnCallback()
                    ? ExecutableEvent.Type.EXECUTABLE_WITH_CALLBACK
                    : ExecutableEvent.Type.EXECUTABLE;
            worker.addExecutable(new ExecutableEventImpl(executable, type));
        }
        return new AllDeferredImpl<Executable>(executables);
    }

    @Override
    public Deferred<?> callback(final Executable executable, final ExecutionContext context) {
        final ThreadId threadId = executable.getThreadId();
        Worker worker;
        if (threadId == null) {
            worker = getWorker();
        } else {
            worker = getCallbackWorker(threadId);
        }
        worker.addExecutable(new ExecutableEventImpl(executable, context, ExecutableEvent.Type.CALLBACK));
        return executable;
    }

    @Override
    public abstract Worker getWorker(final ThreadId threadId);

    @Override
    public abstract Worker getCallbackWorker(final ThreadId threadId);

    @Override
    public abstract List<Worker> getWorkers(final int required);

    @Override
    public abstract Worker getWorker();

    @Override
    public abstract Worker createWorker();

    @Override
    public Future<?> cancel(final Deferred<?> deferred) {
        return cancellation.submit(new Runnable() {
            @Override
            public void run() {
                deferred.cancel(true);
            }
        });
    }

    public abstract static class BaseWorker<T extends Executor> extends Thread implements Worker {
        protected final ThreadIdImpl threadId = new ThreadIdImpl(this);
        protected final Object lock = new Object();
        protected volatile boolean running = true;
        protected final Queue<ExecutableEvent> executables = new LinkedList<ExecutableEvent>();
        protected final Notify notify = new Notify(lock);
        protected int awaiting = 0;
        protected final T executor;

        protected BaseWorker(final T executor) {
            this.executor = executor;
        }

        @Override
        public ThreadId getThreadId() {
            return threadId;
        }

        @Override
        public void addExecutable(final ExecutableEvent event) {
            log.debugf(Messages.get("CHAINLINK-024005.worker.add.executable"), threadId, event.getExecutable());
            switch (event.getType()) {
                case CALLBACK:
                    --this.awaiting;
                    break;
                case EXECUTABLE_WITH_CALLBACK:
                    ++this.awaiting;
                    break;
            }
            synchronized (lock) {
                executables.add(event);
                lock.notifyAll();
            }
        }

        @Override
        public void run() {
            while (running || awaiting > 0) {
                runExecutable();
                awaitIfEmpty();
            }
        }

        void runExecutable() {
            final ExecutableEvent event = _nextFromQueue(executables);
            if (event == null) {
                return;
            }
            final Executable executable = event.getExecutable();
            try {
                executable.always(notify);
                preExecute(event);
                executable.execute(executor, threadId, event);
            } catch (final Throwable e) {
                log.errorf(e, Messages.get("CHAINLINK-024004.worker.execute.execution"), threadId, executable);
            }
        }

        protected void preExecute(final ExecutableEvent event) {
            //noop
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

        @Override
        public synchronized void start() {
            super.start();
            this.addToThreadPool();
        }

        protected abstract void addToThreadPool();

        protected abstract void removeFromThreadPool();

        @Override
        public String toString() {
            return getClass().getSimpleName() + "[threadId=" + threadId + ",running=" + running + ",queued=" + executables.size() + "]";
        }
    }
}
