package io.machinecode.chainlink.transport.infinispan;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.transport.LocalTransport;
import io.machinecode.chainlink.transport.infinispan.cmd.CleanupCommand;
import io.machinecode.chainlink.transport.infinispan.callable.FindExecutionRepositoryCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindWorkerCallable;
import io.machinecode.chainlink.transport.infinispan.callable.LeastBusyWorkerCallable;
import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.util.Pair;
import org.infinispan.AdvancedCache;
import org.infinispan.commands.CommandsFactory;
import org.infinispan.commands.ReplicableCommand;
import org.infinispan.commons.util.concurrent.NotifyingFutureImpl;
import org.infinispan.commons.util.concurrent.NotifyingNotifiableFuture;
import org.infinispan.distexec.DefaultExecutorService;
import org.infinispan.distexec.DistributedExecutorService;
import org.infinispan.lifecycle.ComponentStatus;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.responses.SuccessfulResponse;
import org.infinispan.remoting.rpc.RpcManager;
import org.infinispan.remoting.rpc.RpcOptions;
import org.infinispan.remoting.transport.Address;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanTransport extends LocalTransport {

    final RpcManager rpc;
    final CommandsFactory commands;
    final Address local;
    final String cacheName;
    final AdvancedCache<Object, Object> cache;
    final RpcOptions options;
    final DistributedExecutorService distributor;
    final Timer timer;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<WorkerId, Worker>();
    final TMap<ExecutionRepositoryId, ExecutionRepository> remoteRepositories = new THashMap<ExecutionRepositoryId, ExecutionRepository>();
    final TLongObjectMap<List<Pair<DeferredId,Address>>> remoteExecutions = new TLongObjectHashMap<List<Pair<DeferredId,Address>>>();

    public InfinispanTransport(final TransportConfiguration configuration, final EmbeddedCacheManager manager,
                               final RpcOptions options) {
        super();
        if (manager.getStatus() == ComponentStatus.INSTANTIATED) {
            manager.start();
            manager.getGlobalComponentRegistry().start();
        }
        //TODO Add timeout
        while (manager.getStatus().ordinal() < ComponentStatus.RUNNING.ordinal()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        this.local = manager.getAddress();
        manager.getGlobalComponentRegistry().registerComponent(this, InfinispanTransport.class);
        this.cacheName = configuration.getProperty(InfinispanConstants.CACHE_NAME, InfinispanTransport.class.getCanonicalName());
        this.cache = manager.<Object, Object>getCache(this.cacheName, true).getAdvancedCache();
        this.rpc = cache.getRpcManager();
        this.commands = cache.getComponentRegistry().getCommandsFactory();
        this.options = options;
        this.distributor = new DefaultExecutorService(cache);
        this.timer = new Timer("Chainlink - Infinispan Eviction Timer", true);
    }

    @Override
    public void startup() {
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
    public void shutdown() {
        timer.cancel();
    }

    @Override
    public WorkerId generateWorkerId(final Worker worker) {
        if (worker instanceof Thread) {
            return new InfinispanThreadWorkerId((Thread)worker, local);
        } else {
            return new InfinispanUUIDWorkerId(UUID.randomUUID(), local);
        }
    }

    @Override
    public ExecutionRepositoryId generateExecutionRepositoryId(final ExecutionRepository repository) {
        return new InfinispanUUIDExecutionRepositoryId(UUID.randomUUID(), local);
    }

    //TODO This is wrong. It needs to detect if the call is from this node or not
    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        final ExecutionRepository repository = super.getExecutionRepository(id);
        if (repository != null) {
            return repository;
        }
        return _getRpcExecutionRepository(id);
    }

    private ExecutionRepository _getRpcExecutionRepository(final ExecutionRepositoryId id) {
        final ExecutionRepository repository = remoteRepositories.get(id);
        if (repository != null){
            return repository;
        }
        if (id instanceof InfinispanUUIDExecutionRepositoryId) {
            final ExecutionRepository executionRepository = new RemoteExecutionRepository(this, id, local);
            remoteRepositories.put(id, executionRepository);
            return executionRepository;
        }
        final List<Address> members = rpc.getMembers();
        final List<Future<Address>> futures = new ArrayList<Future<Address>>();
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindExecutionRepositoryCallable(id)));
        }
        for (final Future<Address> future : futures) {
            try {
                final Address address = future.get();
                if (address == null) {
                    continue;
                }
                final RemoteExecutionRepository repo = new RemoteExecutionRepository(this, id, address);
                remoteRepositories.put(id, repo);
                return repo;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    public ExecutionRepository getLocalRepository(final ExecutionRepositoryId id) {
        return super.getExecutionRepository(id);
    }

    @Override
    public void registerJob(final long jobExecutionId, final Deferred<?> deferred) {
        this.remoteExecutions.put(jobExecutionId, new ArrayList<Pair<DeferredId, Address>>());
        super.registerJob(jobExecutionId, deferred);
    }

    @Override
    public void unregisterJob(final long jobExecutionId) throws JobExecutionNotRunningException {
        super.unregisterJob(jobExecutionId);
        for (final Pair<DeferredId, Address> pair : remoteExecutions.remove(jobExecutionId)) {
            final Address address = pair.getValue();
            if (address.equals(local)) {
                this.getDeferred(jobExecutionId, pair.getName());
            } else {
                rpc.invokeRemotely(
                        Collections.singleton(address),
                        new CleanupCommand(this.cacheName, address, jobExecutionId),
                        options
                );
            }
        }
    }

    @Override
    public Worker getWorker(final WorkerId workerId) {
        final Worker worker = super.getWorker(workerId);
        if (worker != null) {
            return worker;
        }
        return _getRpcWorker(workerId);
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        return _anyRpcWorkers(required);
    }

    //TODO
    public InfinispanThreadWorkerId leastBusyWorker() {
        return (InfinispanThreadWorkerId) getWorker().getWorkerId();
    }

    //TODO
    protected List<Address> filterMembers(final List<Address> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    public List<Worker> _anyRpcWorkers(final int required) {
        final List<Address> members = rpc.getMembers();
        final List<Future<InfinispanThreadWorkerId>> futures = new ArrayList<Future<InfinispanThreadWorkerId>>(required);
        for (final Address address : filterMembers(members, required)) {
            futures.add(distributor.submit(address, new LeastBusyWorkerCallable()));
        }
        final ArrayList<Worker> workers = new ArrayList<Worker>(required);
        for (final Future<InfinispanThreadWorkerId> future : futures) {
            try {
                final InfinispanThreadWorkerId threadId = future.get();
                if (threadId.address.equals(local)) {
                    workers.add(super.getWorker(threadId));
                } else {
                    workers.add(new RpcWorker(this, this.local, threadId.address, threadId));
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return workers;
    }

    public Worker _getRpcWorker(final WorkerId workerId) {
        final Worker remoteWorker = remoteWorkers.get(workerId);
        if (remoteWorker != null) {
            return remoteWorker;
        }
        Address remote = null;
        if (workerId instanceof InfinispanThreadWorkerId) {
            remote = ((InfinispanThreadWorkerId) workerId).address;
        }
        if (remote != null) {
            if (remote.equals(local)) {
                return super.getWorker(workerId);
            }
            final Worker rpcWorker = new RpcWorker(this, this.local, remote, workerId);
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
                final Address address = future.get();
                if (address == null) {
                    continue;
                }
                final Worker rpcWorker;
                if (address.equals(local)) {
                    rpcWorker = super.getWorker(workerId);
                } else {
                    rpcWorker = new RpcWorker(this, this.local, address, workerId);
                    remoteWorkers.put(workerId, rpcWorker);
                }
                return rpcWorker;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO message
    }

    public Address getLocal() {
        return local;
    }

    public boolean hasWorker(final WorkerId workerId) {
        while (!workerLock.compareAndSet(false, true)) {}
        try {
            return workers.containsKey(workerId) || workers.containsKey(workerId);
        } finally {
            workerLock.set(false);
        }
    }

    public boolean hasExecutionRepository(final ExecutionRepositoryId executionRepositoryId) {
        while (!repositoryLock.compareAndSet(false, true)) {}
        try {
            return repositories.containsKey(executionRepositoryId);
        } finally {
            repositoryLock.set(false);
        }
    }

    public Object invokeSync(final Address address, final ReplicableCommand command) {
        final Response response = rpc.invokeRemotely(
                Collections.singleton(address),
                command,
                options
        ).get(address);
        if (!response.isSuccessful()) {
            throw new IllegalStateException(); //TODO Message
        }
        return ((SuccessfulResponse)response).getResponseValue();
    }

    public NotifyingNotifiableFuture<Object> invokeAsync(final Address address, final ReplicableCommand command) {
        final NotifyingNotifiableFuture<Object> future = new NotifyingFutureImpl<Object>(null);
        rpc.invokeRemotelyInFuture(
                Collections.singleton(address),
                command,
                options,
                future
        );
        return future;
    }
}
