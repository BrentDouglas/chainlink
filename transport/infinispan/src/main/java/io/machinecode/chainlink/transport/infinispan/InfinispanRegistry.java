package io.machinecode.chainlink.transport.infinispan;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.registry.ExecutableAndContext;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.When;
import io.machinecode.chainlink.transport.infinispan.callable.FindExecutableAndContextCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindExecutionRepositoryWithIdCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindWorkerCallable;
import io.machinecode.chainlink.transport.infinispan.callable.LeastBusyWorkerCallable;
import io.machinecode.chainlink.transport.infinispan.cmd.CleanupCommand;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.infinispan.AdvancedCache;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.factories.GlobalComponentRegistry;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.rpc.ResponseMode;
import org.infinispan.remoting.rpc.RpcManager;
import org.infinispan.remoting.rpc.RpcOptions;
import org.infinispan.remoting.rpc.RpcOptionsBuilder;
import org.infinispan.remoting.transport.Address;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanRegistry extends LocalRegistry {

    private static final Logger log = Logger.getLogger(InfinispanRegistry.class);

    final RpcManager rpc;
    final Address local;
    final String cacheName;
    final AdvancedCache<Object, Object> cache;
    final RpcOptions options;
    final DistributedExecutorService distributor;
    final When network;
    final When reaper;

    final long timeout;
    final TimeUnit unit;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<WorkerId, Worker>();
    final TLongObjectMap<List<Pair<ChainId,Address>>> remoteExecutions = new TLongObjectHashMap<List<Pair<ChainId,Address>>>();

    public InfinispanRegistry(final RegistryConfiguration configuration, final EmbeddedCacheManager manager,
                              final long timeout, final TimeUnit unit) throws Exception {
        final GlobalComponentRegistry gcr = manager.getGlobalComponentRegistry();
        gcr.registerComponent(this, InfinispanRegistry.class); //TODO One Registry per config, this will be an issue
        if (manager.getStatus() == ComponentStatus.INSTANTIATED) {
            manager.start();
            gcr.start();
        }
        //TODO Add timeout
        while (manager.getStatus().ordinal() < ComponentStatus.RUNNING.ordinal()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.network= configuration.getWhenFactory().produce(configuration);
        this.reaper = configuration.getWhenFactory().produce(configuration);
        this.local = manager.getAddress();
        this.cacheName = configuration.getProperty(InfinispanConstants.CACHE, InfinispanConstants.CACHE);
        this.cache = manager.<Object, Object>getCache(this.cacheName, true).getAdvancedCache();
        this.rpc = cache.getRpcManager();
        this.options = new RpcOptions(
                timeout,
                unit,
                null,
                ResponseMode.SYNCHRONOUS,
                false,
                true,
                false
        );
        this.distributor = new DefaultExecutorService(cache);
        this.timeout = Long.parseLong(configuration.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(configuration.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));
    }

    @Override
    public void startup() {
        super.startup();
        final EmbeddedCacheManager manager = cache.getCacheManager();
        if (!manager.isRunning(this.cacheName)) {
            manager.startCaches(this.cacheName);
        }
        //TODO Add timeout
        while (!manager.isRunning(this.cacheName)) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onRegisterJob(final long jobExecutionId) {
        remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, Address>>());
    }

    @Override
    protected Promise<?,?> onUnregisterJob(final long jobExecutionId, final Chain<?> job) {
        final Promise<Object, Throwable> promise = new PromiseImpl<Object, Throwable>().onComplete(new OnComplete() {
            @Override
            public void complete() {
                for (final Pair<ChainId, Address> pair : remoteExecutions.remove(jobExecutionId)) {
                    final Address address = pair.getValue();
                    if (!address.equals(local)) {
                        rpc.invokeRemotely(
                                Collections.singleton(address),
                                new CleanupCommand(cacheName, jobExecutionId),
                                options
                        );
                    }
                }
                log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
            }
        });
        this.reaper.when((Future<Object>) job, promise);
        return promise;
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public ChainId generateChainId() {
        return new InfinispanUUIDId(local);
    }

    @Override
    public ExecutableId generateExecutableId() {
        return new InfinispanUUIDId(local);
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new InfinispanWorkerId((Thread)worker, local);
        } else {
            return new InfinispanUUIDId(local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId() {
        return new InfinispanUUIDId(local);
    }

    private Worker _localWorker(final WorkerId workerId) {
        return super.getWorker(workerId);
    }

    @Override
    public Worker getWorker(final WorkerId workerId) {
        final Worker worker = _localWorker(workerId);
        if (worker != null) {
            return worker;
        }
        final Worker remoteWorker = remoteWorkers.get(workerId);
        if (remoteWorker != null) {
            return remoteWorker;
        }
        Address remote = null;
        if (workerId instanceof InfinispanWorkerId) {
            remote = ((InfinispanWorkerId) workerId).getAddress();
        }
        if (remote != null) {
            if (remote.equals(local)) {
                throw new IllegalStateException(); //This should have been handled at the start
            }
            final Worker rpcWorker = new InfinispanWorker(this, local, remote, workerId);
            remoteWorkers.put(workerId, rpcWorker);
            return rpcWorker;
        }
        final List<Address> members = rpc.getMembers();
        final List<Future<Address>> futures = new ArrayList<Future<Address>>();
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindWorkerCallable(workerId)));
        }
        for (final Future<Address> future : futures) {
            try {
                //TODO Search these for completes rather that .get() them in order
                final Address address = future.get(this.timeout, this.unit);
                if (address == null) {
                    continue;
                }
                final Worker rpcWorker;
                if (address.equals(local)) {
                    throw new IllegalStateException(); //Also should not have been distributed
                } else {
                    rpcWorker = new InfinispanWorker(this, local, address, workerId);
                    remoteWorkers.put(workerId, rpcWorker);
                }
                return rpcWorker;
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO message
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final List<Address> members = rpc.getMembers();
        final List<Future<InfinispanWorkerId>> futures = new ArrayList<Future<InfinispanWorkerId>>(required);
        for (final Address address : filterMembers(members, required)) {
            futures.add(distributor.submit(address, new LeastBusyWorkerCallable()));
        }
        final ArrayList<Worker> workers = new ArrayList<Worker>(required);
        for (final Future<InfinispanWorkerId> future : futures) {
            try {
                final InfinispanWorkerId threadId = future.get(this.timeout, this.unit);
                if (local.equals(threadId.getAddress())) {
                    workers.add(getWorker(threadId));
                } else {
                    workers.add(new InfinispanWorker(this, local, threadId.getAddress(), threadId));
                }
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return workers;
    }

    @Override
    public Chain<?> getJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        //Todo Remote
        return super.getJob(jobExecutionId);
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        final ExecutionRepository ours = super.getExecutionRepository(id);
        if (ours != null) {
            return ours;
        }
        final List<Address> members = new ArrayList<Address>(rpc.getMembers());
        members.remove(this.local);
        final List<Future<Address>> futures = new ArrayList<Future<Address>>(members.size());
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindExecutionRepositoryWithIdCallable(id)));
        }
        for (final Future<Address> future : futures) {
            try {
                final Address address = future.get(this.timeout, this.unit);
                if (address == null) {
                    continue;
                } else if (local.equals(address)) {
                    throw new IllegalStateException(); //TODO Message
                }
                return new InfinispanExecutionRepositoryProxy(this, id, address);
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public ExecutableAndContext getExecutableAndContext(final long jobExecutionId, final ExecutableId id) {
        final ExecutableAndContext ours = super.getExecutableAndContext(jobExecutionId, id);
        if (ours != null) {
            return ours;
        }
        final List<Address> members = new ArrayList<Address>(rpc.getMembers());
        members.remove(this.local);
        final List<Future<ExecutableAndContext>> futures = new ArrayList<Future<ExecutableAndContext>>(members.size());
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindExecutableAndContextCallable(jobExecutionId, id)));
        }
        for (final Future<ExecutableAndContext> future : futures) {
            try {
                final ExecutableAndContext executable = future.get(this.timeout, this.unit);
                if (executable == null) {
                    continue;
                }
                return executable;
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    public ExecutionRepository getLocalExecutionRepository(final ExecutionRepositoryId id) {
        return  super.getExecutionRepository(id);
    }

    //TODO
    protected List<Address> filterMembers(final List<Address> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    public InfinispanWorkerId leastBusyWorker() {
        return (InfinispanWorkerId)getWorker().id();
    }

    public Address getLocal() {
        return local;
    }

    public boolean hasWorker(final WorkerId workerId) {
        return _localWorker(workerId) != null;
    }

    public <T> void invoke(final Address address, final ReplicableCommand command, final Promise<T,Throwable> promise) {
        rpc.invokeRemotelyInFuture(
                Collections.singleton(address),
                command,
                options,
                new InfinispanFuture<Object, T>(this, promise, address, System.currentTimeMillis())
        );
    }

    public <T> void invoke(final Address address, final ReplicableCommand command, final Promise<T,Throwable> promise, final long timeout, final TimeUnit unit) {
        rpc.invokeRemotelyInFuture(
                Collections.singleton(address),
                command,
                new RpcOptionsBuilder(options).timeout(timeout, unit).build(),
                new InfinispanFuture<Object,T>(this, promise, address, System.currentTimeMillis())
        );
    }
}
