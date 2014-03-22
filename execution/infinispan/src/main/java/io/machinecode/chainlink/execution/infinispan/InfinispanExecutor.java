package io.machinecode.chainlink.execution.infinispan;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import org.infinispan.AdvancedCache;
import org.infinispan.commands.CommandsFactory;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commons.util.concurrent.NoOpFuture;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.rpc.ResponseMode;
import org.infinispan.remoting.rpc.RpcManager;
import org.infinispan.remoting.rpc.RpcOptions;
import org.infinispan.remoting.transport.Address;

import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.TransactionManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanExecutor implements Executor {

    public static final String CACHE_NAME = InfinispanExecutor.class.getCanonicalName();

    final RpcManager rpc;
    final CommandsFactory commands;
    final Address local;
    final AdvancedCache<Address, InfinispanExecutor> cache;
    final Executor delegate;
    final RpcOptions options;
    final DistributedExecutorService distributor;

    final Timer timer;

    final TMap<ThreadId, Address> addresses = new THashMap<ThreadId, Address>();
    final TMap<UUID, Deferred<?>> remotes = new THashMap<UUID, Deferred<?>>();

    public InfinispanExecutor(final EmbeddedCacheManager manager, final Executor delegate) {
        this.local = manager.getAddress();
        this.cache = manager.<Address, InfinispanExecutor>getCache(CACHE_NAME, true)
                .getAdvancedCache();
        this.rpc = cache.getRpcManager();
        this.commands = cache.getComponentRegistry().getCommandsFactory();
        this.delegate = delegate;
        this.options = new RpcOptions(
                180,
                TimeUnit.SECONDS,
                null,
                ResponseMode.ASYNCHRONOUS,
                true,
                true,
                true
        );
        this.distributor = new DefaultExecutorService(cache);
        this.timer = new Timer("Chainlink - Infinispan Eviction Timer", true);
    }

    @Override
    public void start() {
        //TODO Ensure cache is started
        this.cache.put(this.local, this);
        delegate.start();
    }

    @Override
    public void stop() {
        this.cache.remove(this.local);
        delegate.stop();
    }

    @Override
    public TransactionManager getTransactionManager() {
        return delegate.getTransactionManager();
    }

    @Override
    public ExecutionRepository getRepository() {
        return delegate.getRepository();
    }

    @Override
    public InjectionContext getInjectionContext() {
        return delegate.getInjectionContext();
    }

    @Override
    public Deferred<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        return delegate.getJob(jobExecutionId);
    }

    @Override
    public Deferred<?> removeJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        return delegate.removeJob(jobExecutionId);
    }

    @Override
    public Deferred<?> execute(final long jobExecutionId, final Executable executable) {
        return delegate.execute(jobExecutionId, executable);
    }

    @Override
    public Deferred<?> execute(final Executable executable) {
        return delegate.execute(executable);
    }

    @Override
    public Deferred<?> distribute(final int maxThreads, final Executable... executables) {
        return delegate.distribute(maxThreads, executables);
    }

    @Override
    public Deferred<?> callback(final Executable executable, final ExecutionContext context) {
        return delegate.callback(executable, context);
    }

    @Override
    public Worker createWorker() {
        return new InfinispanWorker(local, delegate.createWorker());
    }

    @Override
    public Worker getWorker(final ThreadId threadId) {
        final Worker worker = delegate.getWorker(threadId);
        if (worker != null) {
            return worker;
        }
        return _getRpcWorker(threadId);
    }

    @Override
    public Worker getCallbackWorker(final ThreadId threadId) {
        final Worker worker = delegate.getCallbackWorker(threadId);
        if (worker != null) {
            return worker;
        }
        return _getRpcWorker(threadId);
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        return delegate.getWorkers(required);
    }

    @Override
    public Worker getWorker() {
        return delegate.getWorker();
    }

    public InfinispanThreadId leastBusyWorker() {
        return null; //TODO
    }

    public List<Worker> _anyRpcWorkers(final int required) {
        //TODO Should be submit to 'required' nodes. Should exclude the current node which should not be executed by rpc
        final List<Future<InfinispanThreadId>> futures = distributor.submitEverywhere(new BaseCallable<Address, InfinispanExecutor, InfinispanThreadId>() {
            @Override
            public InfinispanThreadId call() throws Exception {
                final Address address = cache.getCacheManager().getAddress();
                return cache.get(address).leastBusyWorker();
            }
        });
        final ArrayList<Worker> workers = new ArrayList<Worker>(required);
        for (final Future<InfinispanThreadId> future : futures) {
            //Determine best threads
            final InfinispanThreadId threadId;
            try {
                threadId = future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
            workers.add(new RpcWorker(this, this.local, threadId.address, threadId));
        }
        return workers;
    }

    public Worker _getRpcWorker(final ThreadId threadId) {
        Address remote = null;
        if (threadId instanceof InfinispanThreadId) {
            remote = ((InfinispanThreadId)threadId).address;
        }
        if (remote != null) {
            return new RpcWorker(this, this.local, remote, threadId);
        }
        remote = addresses.get(threadId);
        if (remote != null) {
            return new RpcWorker(this, this.local, remote, threadId);
        }
        throw new IllegalStateException(); //TODO message
    }

    @Override
    public Future<?> cancel(final Deferred<?> deferred) {
        return delegate.cancel(deferred);
    }

    public void registerDeferred(final UUID uuid, final Deferred<?> deferred, final long seconds) {
        remotes.put(uuid, deferred);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                final Deferred<?> that = unregisterDeferred(uuid);
                if (that == null) {
                    return;
                }
                that.reject(new TimeoutException()); //TODO message
            }
        }, TimeUnit.SECONDS.toMillis(seconds));
    }

    public Deferred<?> unregisterDeferred(final UUID uuid) {
        return remotes.remove(uuid);
    }

    public void invoke(final Address address, final ReplicableCommand command) {
        rpc.invokeRemotelyInFuture(
                Collections.singleton(address),
                command,
                options,
                new NoOpFuture<Object>(null)
        );
    }

    private static class InfinispanWorker implements Worker {
        private final InfinispanThreadId threadId;
        private final Worker delegate;

        private InfinispanWorker(final Address address, final Worker delegate) {
            this.threadId = new InfinispanThreadId(delegate.getThreadId(), address);
            this.delegate = delegate;
        }

        @Override
        public InfinispanThreadId getThreadId() {
            return threadId;
        }

        @Override
        public void addExecutable(final ExecutableEvent event) {
            delegate.addExecutable(event);
        }

        @Override
        public void start() {
            delegate.start();
        }

        @Override
        public void run() {
            delegate.run();
        }
    }
}
