package io.machinecode.nock.core.local;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.core.inject.InjectionContextImpl;
import io.machinecode.nock.core.work.AllDeferredImpl;
import io.machinecode.nock.core.work.AnyDeferredImpl;
import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.Notify;
import io.machinecode.nock.spi.ExecutionRepository;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Result;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Bucket;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransport implements Transport {

    private static final Logger log = Logger.getLogger(LocalTransport.class);

    protected static final LocalTransactionManager TRANSACTION_MANAGER = new LocalTransactionManager(180); // @see 9.7

    protected static final LocalWork[] NO_WORK = new LocalWork[0];

    protected final ExecutionRepository repository;
    protected final InjectionContext injectionContext;
    protected final IdentityHashMap<TaskWork, Bucket> buckets = new IdentityHashMap<TaskWork, Bucket>();

    protected final List<Worker> workers;
    protected final IdentityHashMap<Executable, Worker> executables = new IdentityHashMap<Executable, Worker>();
    protected final IdentityHashMap<Thread, Worker> threads = new IdentityHashMap<Thread, Worker>();

    protected final IdentityHashMap<Executable, List<LocalWork>> children = new IdentityHashMap<Executable, List<LocalWork>>();
    protected final TMap<Long, List<Executable>> jobChildren = new THashMap<Long, List<Executable>>();
    protected final TMap<Long, Deferred<?>> jobs = new THashMap<Long, Deferred<?>>();
    protected final TMap<Long, Context> contexts = new THashMap<Long, Context>();
    protected final AtomicBoolean childLock = new AtomicBoolean(false);
    protected final AtomicBoolean jobLock = new AtomicBoolean(false);
    protected final AtomicBoolean contextLock = new AtomicBoolean(false);

    private Deferred<?> _getJob(final long executionId) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            return this.jobs.get(executionId);
        } finally {
            jobLock.set(false);
        }
    }

    private Deferred<?> _putJob(final long jobExecutionId, final Deferred<?> deferred) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            return this.jobs.put(jobExecutionId, deferred);
        } finally {
            jobLock.set(false);
        }
    }

    private Deferred<?> _evictJob(final long jobExecutionId) {
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            return this.jobs.remove(jobExecutionId);
        } finally {
            jobLock.set(false);
        }
    }

    private Context _getContext(final long jobExecutionId) {
        while (!contextLock.compareAndSet(false, true)) {}
        try {
            return this.contexts.get(jobExecutionId);
        } finally {
            contextLock.set(false);
        }
    }

    private Context _putContext(final long jobExecutionId, final Context context) {
        while (!contextLock.compareAndSet(false, true)) {}
        try {
            return this.contexts.put(jobExecutionId, context);
        } finally {
            contextLock.set(false);
        }
    }

    private Context _evictContext(final long jobExecutionId) {
        while (!contextLock.compareAndSet(false, true)) {}
        try {
            return this.contexts.remove(jobExecutionId);
        } finally {
            contextLock.set(false);
        }
    }

    private List<LocalWork> _popChildren(final Executable parent) {
        while (!childLock.compareAndSet(false, true)) {}
        try {
            final List<LocalWork> works = children.remove(parent);
            return (works == null)
                    ? Collections.<LocalWork>emptyList()
                    : works;
        } finally {
            childLock.set(false);
        }
    }

    private LocalWork _addChild(final long jobExecutionId, final Executable parent, final LocalWork localWork) {
        // This means it is either a job level executable or it is a
        // lifecycle method of another task and will be queued by the
        // worker as required.
        if (parent == null) {
            return localWork;
        }
        while (!childLock.compareAndSet(false, true)) {}
        try {
            List<Executable> works = jobChildren.get(jobExecutionId);
            if (works == null) {
                works = new ArrayList<Executable>(1);
                jobChildren.put(jobExecutionId, works);
            }
            works.add(parent);

            List<LocalWork> that = children.get(parent);
            if (that == null) {
                that = new ArrayList<LocalWork>(1);
                children.put(parent, that);
            }
            that.add(localWork);

            return localWork;
        } finally {
            childLock.set(false);
        }
    }

    private void _evictChildren(final long jobExecutionId) {
        while (!childLock.compareAndSet(false, true)) {}
        try {
            final List<Executable> works = jobChildren.get(jobExecutionId);
            if (works == null) {
                return;
            }
            for (final Executable work : works) {
                children.remove(work);
            }
        } finally {
            childLock.set(false);
        }
    }

    public LocalTransport(final RuntimeConfiguration configuration, final int threads) {
        this.repository = configuration.getRepository();
        this.injectionContext = new InjectionContextImpl(configuration);
        this.workers = new ArrayList<Worker>(threads);
        for (int i = 0; i < threads; ++i) {
            final Worker worker = new Worker();
            this.workers.add(worker);
            worker.start();
        }
    }

    @Override
    public TransactionManager getTransactionManager() {
        return TRANSACTION_MANAGER;
    }

    @Override
    public ExecutionRepository getRepository() {
        return repository;
    }

    @Override
    public Context getContext(final long jobExecutionId) {
        return _getContext(jobExecutionId);
    }

    @Override
    public Deferred<?> getJob(final long jobExecutionId) {
        return _getJob(jobExecutionId);
    }

    @Override
    public Deferred<?> execute(final long jobExecutionId, final Executable parent, final Plan plan) {
        final int len = plan.getExecutables().length;
        if (len == 1) {
            return _work(jobExecutionId, parent, plan).executable;
        } else {
            final Deferred[] results = new Deferred[len];
            final LocalWork[] works = _works(jobExecutionId, parent, plan);
            for (int i = 0; i < works.length; ++i) {
                results[i] = works[i].executable;
            }
            return new DeferredImpl<Void>(results);
        }
    }

    @Override
    public Deferred<?> executeJob(final long jobExecutionId, final JobWork job, final Context context) {
        final Plan plan = job.plan(this, context);
        final Deferred<?> that = _work(jobExecutionId, null, plan).enqueue();
        _putJob(jobExecutionId, that);
        _putContext(jobExecutionId, context);
        return that;
    }

    @Override
    public void finalizeJob(final long jobExecutionId) {
        _evictChildren(jobExecutionId);
        _evictContext(jobExecutionId);
        final Deferred<?> job = _evictJob(jobExecutionId);
        synchronized (job) {
            job.notifyAll();
        }
    }

    @Override
    public InjectionContext createInjectionContext(final Context context) {
        return injectionContext;
    }

    @Override
    public Bucket getBucket(final TaskWork work) {
        return this.buckets.get(work);
    }

    @Override
    public void setBucket(final Bucket bucket, final TaskWork work) {
       this.buckets.put(work, bucket);
    }

    @Override
    public Bucket evictBucket(final TaskWork work) {
        return this.buckets.remove(work);
    }

    private LocalWork _work(final long jobExecutionId, final Executable parent, final Plan plan) {
        final TargetThread target = plan.getTargetThread();
        final Executable targetExecutable = target.getExecutable();

        final Executable executable = plan.getExecutables()[0];

        final Worker worker;
        switch (target.getType()) {
            case THIS:
                worker = threads.get(Thread.currentThread());
                break;
            case THAT:
                worker = this.executables.get(targetExecutable);
                break;
            case ANY:
            default:
                worker = _leastBusy(workers);
        }
        if (worker == null) {
            throw new IllegalStateException(target.getType() + ""); //TODO Message
        }
        this.executables.put(executable, worker);

        final Plan[] planThen = plan.then();
        final LocalWork[] then;
        if (planThen != null) {
            then = new LocalWork[planThen.length];
            for (int i = 0; i < then.length; ++i) {
                then[i] = _work(jobExecutionId, null, planThen[i]);
                //then[i].executable.listener(executable);
            }
        } else {
            then = NO_WORK;
        }
        final Plan[] planFail = plan.fail();
        final LocalWork[] fail;
        if (planFail != null) {
            fail = new LocalWork[planFail.length];
            for (int i = 0; i < fail.length; ++i) {
                fail[i] = _work(jobExecutionId, null, planFail[i]);
            }
        } else {
            fail = NO_WORK;
        }
        final Plan[] planAlways = plan.always();
        final LocalWork[] always;
        if (planAlways != null) {
            always = new LocalWork[planAlways.length];
            for (int i = 0; i < always.length; ++i) {
                always[i] = _work(jobExecutionId, null, planAlways[i]);
            }
        } else {
            always = NO_WORK;
        }
        return _addChild(jobExecutionId, parent, new LocalWork(
                executable,
                worker,
                then,
                fail,
                always
        ));
    }

    private LocalWork[] _works(final long jobExecutionId, final Executable parent, final Plan plan) {
        final TargetThread target = plan.getTargetThread();
        final Executable targetExecutable = target.getExecutable();

        final Plan[] planThen = plan.then();
        final LocalWork[] then;
        if (planThen != null) {
            then = new LocalWork[planThen.length];
        } else {
            then = NO_WORK;
        }
        final Plan[] planFail = plan.fail();
        final LocalWork[] fail;
        if (planFail != null) {
            fail = new LocalWork[planFail.length];
        } else {
            fail = NO_WORK;
        }
        final Plan[] planAlways = plan.always();
        final LocalWork[] always;
        if (planAlways != null) {
            always = new LocalWork[planAlways.length];
        } else {
            always = NO_WORK;
        }

        final Set<Worker> used = Collections.newSetFromMap(new IdentityHashMap<Worker, Boolean>());
        final LocalWork[] works = new LocalWork[plan.getExecutables().length];
        final int maxThreads = plan.getMaxThreads();
        for (int i = 0; i < works.length; ++i) {
            final Executable executable = plan.getExecutables()[i];
            final Worker worker;
            switch (target.getType()) {
                case THIS:
                    worker = threads.get(Thread.currentThread());
                    break;
                case THAT:
                    worker = this.executables.get(targetExecutable);
                    break;
                case ANY:
                default:
                    worker = _leastBusy(used.size() >= maxThreads ? used : workers);
            }
            if (worker == null) {
                throw new IllegalStateException(); //TODO Message
            }
            this.executables.put(executable, worker);
            works[i] = new LocalWork(
                    executable,
                    worker,
                    then,
                    fail,
                    always
            );
        }

        for (int i = 0; i < then.length; ++i) {
            then[i] = _work(jobExecutionId, null, planThen[i]);
        }
        for (int i = 0; i < fail.length; ++i) {
            fail[i] = _work(jobExecutionId, null, planFail[i]);
        }
        for (int i = 0; i < always.length; ++i) {
            always[i] = _work(jobExecutionId, null, planAlways[i]);
        }

        return works;
    }

    private Worker _leastBusy(final Collection<Worker> workers) {
        int leastQueueSize;
        Worker leastBusy = null;
        synchronized (workers) {
            leastQueueSize = workers.size();
            for (final Worker worker : workers) {
                final int queueSize;
                synchronized (worker.stack) {
                    queueSize = worker.stack.size();
                }
                if (queueSize < leastQueueSize || leastBusy == null) {
                    leastBusy = worker;
                    leastQueueSize = queueSize;
                }
            }
        }
        return leastBusy;
    }

    private class LocalWork {
        final Executable executable;
        final Worker worker;
        final LocalWork[] then;
        final LocalWork[] fail;
        final LocalWork[] always;
        private volatile boolean queued = false;

        private LocalWork(final Executable executable, final Worker worker, final LocalWork[] then, final LocalWork[] fail, final LocalWork[] always) {
            this.executable = executable;
            this.worker = worker;
            this.then = then;
            this.fail = fail;
            this.always = always;
        }

        public synchronized Deferred enqueue() {
            if (queued) {
                throw new IllegalStateException();
            }
            queued = true;
            return this.worker.add(this);
        }
    }

    public static class Enqueue extends Notify {
        final LocalWork work;

        private Enqueue(final LocalWork work) {
            super(work.executable);
            this.work = work;
        }

        @Override
        public void run() {
            work.enqueue();
            super.run();
        }
    }

    private class Worker extends Thread {
        private volatile boolean running = true;
        final Stack<LocalWork> stack = new Stack<LocalWork>();

        public Worker() {
            threads.put(this, this);
        }

        public Deferred add(final LocalWork work) {
            try {
                final Deferred<?>[] any = new Deferred[work.then.length + work.fail.length];
                int i = 0;
                for (final LocalWork that : work.then) {
                    any[i] = that.executable;
                }
                for (final LocalWork that : work.fail) {
                    any[i] = that.executable;
                }
                final Deferred<?>[] all = new Deferred[2 + work.always.length];
                all[0] = work.executable;
                all[1] = new AnyDeferredImpl<Void>(any);
                int j = 2;
                for (final LocalWork that : work.always) {
                    all[j++] = that.executable;
                }
                return new AllDeferredImpl<Void>(all);
            } finally {
                synchronized (stack) {
                    stack.push(work);
                    stack.notifyAll();
                }
            }
        }

        @Override
        public void run() {
            while (running) {
                final LocalWork that = _next();
                if (that == null) {
                    continue;
                }
                try {
                    final Result result = that.executable.execute(LocalTransport.this);
                    switch (result.status()) {
                        case FINISHED:
                            _queue(that.then, that.always);
                            for (final LocalWork fail : that.fail) { //TODO This is to ensure that one of the Deferred's in the AnyDeferred is marked as done
                                fail.executable.cancel(true);
                            }
                            _queueChildren(that);
                            break;
                        case ERROR:
                            _queue(that.fail, that.always);
                            log.error("", (Throwable) result.value()); //TODO Message
                            for (final LocalWork then : that.then) { //TODO Resolving deferreds
                                then.executable.cancel(true);
                            }
                            break;
                        case CANCELLED:
                            break;
                        case RUNNING:
                        default:
                            throw new IllegalStateException("was " + result.status()); //TODO Message
                    }
                } catch (final Throwable e) {
                    log.error("", e); //TODO Message
                } finally {
                    LocalTransport.this.executables.remove(that.executable);
                    synchronized (stack) {
                        stack.remove(that);
                    }
                }
            }
        }

        private void _queue(final LocalWork[] nows, final LocalWork[] thens) {
            if (nows.length == 0) {
                for (final LocalWork then : thens) {
                    then.enqueue();
                }
                return;
            }
            for (final LocalWork then : thens) {
                for (final LocalWork now : nows) {
                    now.executable.addListener(new Enqueue(then));
                }
            }
            for (final LocalWork now : nows) {
                now.enqueue();
            }
        }

        private void _queueChildren(final LocalWork that) {
            for (final LocalWork local : _popChildren(that.executable)) {
                local.enqueue();
            }
        }

        private LocalWork _next() {
            synchronized (stack) {
                if (!stack.empty()) {
                    return stack.pop();
                }
                try {
                    stack.wait();
                } catch (final InterruptedException e) {
                    running = false;
                    // TODO log
                }
            }
            return null;
        }
    }
}
