package io.machinecode.nock.core.local;

import io.machinecode.nock.core.inject.InjectionContextImpl;
import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Result;
import io.machinecode.nock.spi.transport.Synchronization;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Bucket;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalTransport implements Transport {

    private static final Logger log = Logger.getLogger(LocalTransport.class);

    private static final LocalTransactionManager TRANSACTION_MANAGER = new LocalTransactionManager(180); // @see 9.7

    private static final Work[] NO_WORK = new Work[0];

    private final Repository repository;
    private final InjectionContext injectionContext;
    private final IdentityHashMap<TaskWork, Bucket> buckets = new IdentityHashMap<TaskWork, Bucket>();

    private final List<Worker> workers;
    private final IdentityHashMap<Executable, Worker> executables = new IdentityHashMap<Executable, Worker>();
    private final IdentityHashMap<Thread, Worker> threads = new IdentityHashMap<Thread, Worker>();

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
    public Repository getRepository() throws Exception {
        return repository;
    }

    @Override
    public Deferred execute(final Plan plan) {
        final int len = plan.getExecutables().length;
        if (len == 1) {
            return _work(plan).enqueue();
        } else {
            final Deferred[] results = new Deferred[len];
            final Work[] works = _works(plan);
            for (int i = 0; i < works.length; ++i) {
                results[i] = works[i].enqueue();
            }
            return new DeferredImpl(results);
        }
    }

    @Override
    public Synchronization wrapSynchronization(final Synchronization synchronization) {
        return synchronization;
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

    private Work _work(final Plan plan) {
        final TargetThread target = plan.getTargetThread();
        final Executable targetExecutable = target.getExecutable();

        final Plan[] planThen = plan.then();
        final Work[] then;
        if (planThen != null) {
            then = new Work[planThen.length];
            for (int i = 0; i < then.length; ++i) {
                then[i] = _work(planThen[i]);
            }
        } else {
            then = NO_WORK;
        }
        final Plan[] planFail = plan.fail();
        final Work[] fail;
        if (planFail != null) {
            fail = new Work[planFail.length];
            for (int i = 0; i < fail.length; ++i) {
                fail[i] = _work(planFail[i]);
            }
        } else {
            fail = NO_WORK;
        }
        final Plan[] planAlways = plan.always();
        final Work[] always;
        if (planAlways != null) {
            always = new Work[planAlways.length];
            for (int i = 0; i < always.length; ++i) {
                always[i] = _work(planAlways[i]);
            }
        } else {
            always = NO_WORK;
        }

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
            throw new IllegalStateException(); //TODO Message
        }
        return new Work(
                executable,
                worker,
                then,
                fail,
                always
        );
    }

    private Work[] _works(final Plan plan) {
        final TargetThread target = plan.getTargetThread();
        final Executable targetExecutable = target.getExecutable();

        final Plan[] planThen = plan.then();
        final Work[] then;
        if (planThen != null) {
            then = new Work[planThen.length];
            for (int i = 0; i < then.length; ++i) {
                then[i] = _work(planThen[i]);
            }
        } else {
            then = NO_WORK;
        }
        final Plan[] planFail = plan.fail();
        final Work[] fail;
        if (planFail != null) {
            fail = new Work[planFail.length];
            for (int i = 0; i < fail.length; ++i) {
                fail[i] = _work(planFail[i]);
            }
        } else {
            fail = NO_WORK;
        }
        final Plan[] planAlways = plan.always();
        final Work[] always;
        if (planAlways != null) {
            always = new Work[planAlways.length];
            for (int i = 0; i < always.length; ++i) {
                always[i] = _work(planAlways[i]);
            }
        } else {
            always = NO_WORK;
        }
        final Set<Worker> used = Collections.newSetFromMap(new IdentityHashMap<Worker, Boolean>());
        final Work[] works = new Work[plan.getExecutables().length];
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
            works[i] = new Work(
                    executable,
                    worker,
                    then,
                    fail,
                    always
            );
        }

        return works;
    }

    private Worker _leastBusy(final Collection<Worker> workers) {
        int current;
        Worker least = null;
        synchronized (workers) {
            current = workers.size();
            for (final Worker worker : workers) {
                final int queue;
                synchronized (worker.queue) {
                    queue = worker.queue.size();
                }
                if (queue < current) {
                    least = worker;
                    current = queue;
                }
            }
        }
        return least;
    }

    private class Work {
        final Executable executable;
        final Worker worker;
        final Work[] then;
        final Work[] fail;
        final Work[] always;

        private Work(final Executable executable, final Worker worker, final Work[] then, final Work[] fail, final Work[] always) {
            this.executable = executable;
            this.worker = worker;
            this.then = then;
            this.fail = fail;
            this.always = always;
            LocalTransport.this.executables.put(executable, worker);
        }

        public Deferred enqueue() {
            return this.worker.add(this);
        }
    }

    private class Worker extends Thread {
        private volatile boolean running = true;
        final List<Work> queue = new ArrayList<Work>();

        public Worker() {
            threads.put(this, this);
        }

        public Deferred add(final Work work) {
            work.executable.listener(queue);
            queue.add(work);
            synchronized (queue) {
                queue.notifyAll();
            }
            return work.executable;
        }

        @Override
        public void run() {
            while (running) {
                final Work that = _next();
                if (that == null) {
                    continue;
                }
                try {
                    final Result result = that.executable.execute(LocalTransport.this);
                    switch (result.status()) {
                        case FINISHED:
                            _queue(that.then, that.always);
                            break;
                        case ERROR:
                            _queue(that.fail, that.always);
                            break;
                        case CANCELLED:
                            break;
                        case RUNNING:
                        default:
                            throw new IllegalStateException(); //TODO Message

                    }
                } finally {
                    synchronized (queue) {
                        queue.remove(that);
                    }
                    LocalTransport.this.executables.remove(that.executable);
                }
            }
        }

        private void _queue(final Work[] nows, final Work[] thens) {
            for (final Work then : thens) {
                for (final Work now : nows) {
                    then.executable.register(now.executable);
                }
            }
            for (final Work now : nows) {
                now.enqueue();
            }
            for (final Work then : thens) {
                then.enqueue();
            }
        }

        private Work _next() {
            synchronized (queue) {
                for (final Work work : queue) {
                    if (work.executable.available()) {
                        return work;
                    }
                }
                try {
                    queue.wait();
                } catch (final InterruptedException e) {
                    running = false;
                    // TODO log
                }
            }
            return null;
        }
    }
}
