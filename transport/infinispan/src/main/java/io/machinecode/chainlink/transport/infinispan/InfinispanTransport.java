package io.machinecode.chainlink.transport.infinispan;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.transport.BaseTransport;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.core.transport.WorkerIdAndAddress;
import io.machinecode.chainlink.core.transport.cmd.CleanupCommand;
import io.machinecode.chainlink.transport.infinispan.callable.FindExecutableCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindExecutionRepositoryWithIdCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindWorkerByIdCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindWorkerForExecutionCallable;
import io.machinecode.chainlink.transport.infinispan.callable.LeastBusyWorkerCallable;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.FutureDeferred;
import org.infinispan.AdvancedCache;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanTransport extends BaseTransport<Address> {

    private static final Logger log = Logger.getLogger(InfinispanTransport.class);
    public static final String CLEANUP_INFINISPAN = "cleanup-infinispan";

    final RpcManager rpc;
    final Address local;
    final String cacheName;
    final AdvancedCache<Object, Object> cache;
    final RpcOptions options;
    final DistributedExecutorService distributor;
    final Executor network;
    final Executor reaper;

    final long timeout;
    final TimeUnit unit;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<>();
    final TLongObjectMap<List<Pair<ChainId,Address>>> remoteExecutions = new TLongObjectHashMap<>();

    public InfinispanTransport(final Dependencies configuration, final Properties properties, final EmbeddedCacheManager manager,
                               final long timeout, final TimeUnit unit) throws Exception {
        super(configuration, properties);
        final GlobalComponentRegistry gcr = manager.getGlobalComponentRegistry();
        gcr.registerComponent(this, InfinispanTransport.class); //TODO One Registry per config, this will be an issue
        gcr.registerComponent(getRegistry(), Registry.class);
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
        this.network = Executors.newSingleThreadExecutor();
        this.reaper = Executors.newSingleThreadExecutor();
        this.local = manager.getAddress();
        this.cacheName = properties.getProperty(InfinispanConstants.CACHE, InfinispanConstants.CACHE);
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
        this.timeout = Long.parseLong(properties.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(properties.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));

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
        this.getRegistry().registerJobEventListener(CLEANUP_INFINISPAN, new JobEventListener() {
            @Override
            public Promise<?, ?, ?> onRegister(final long jobExecutionId, final Chain<?> job) {
                remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, Address>>());
                return null;
            }

            public Promise<?, ?, ?> onUnregister(final long jobExecutionId, final Chain<?> job) {
                final FutureDeferred<Object, Void> promise = new FutureDeferred<>(job, timeout, unit);
                promise.onComplete(new OnComplete() {
                    @Override
                    public void complete(final int state) {
                        for (final Pair<ChainId, Address> pair : remoteExecutions.remove(jobExecutionId)) {
                            final Address address = pair.getValue();
                            if (!address.equals(local)) {
                                rpc.invokeRemotely(
                                        Collections.singleton(address),
                                        new CommandAdapter(cacheName, new CleanupCommand<Address>(jobExecutionId), local),
                                        options
                                );
                            }
                        }
                        log.debugf(Messages.get("CHAINLINK-005101.registry.removed.job"), jobExecutionId);
                    }
                });
                reaper.execute(promise);
                return promise;
            }
        });
    }

    @Override
    public void close() throws Exception {
        this.getRegistry().unregisterJobEventListener(CLEANUP_INFINISPAN);
        super.close();
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

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }

    @Override
    public Worker getWorker(final WorkerId workerId) {
        final Worker worker = getLocalWorker(workerId);
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
        final List<Future<Address>> futures = new ArrayList<>();
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindWorkerByIdCallable(workerId)));
        }
        for (final Future<Address> future : futures) {
            try {
                //TODO Search these for completes rather that .get(...) them in order
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
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO message
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final List<Address> members = rpc.getMembers();
        final List<Future<InfinispanWorkerId>> futures = new ArrayList<>(required);
        for (final Address address : filterMembers(members, required)) {
            futures.add(distributor.submit(address, new LeastBusyWorkerCallable()));
        }
        final ArrayList<Worker> workers = new ArrayList<>(required);
        for (final Future<InfinispanWorkerId> future : futures) {
            try {
                final InfinispanWorkerId threadId = future.get(this.timeout, this.unit);
                if (local.equals(threadId.getAddress())) {
                    workers.add(getLocalWorker(threadId));
                } else {
                    workers.add(new InfinispanWorker(this, local, threadId.getAddress(), threadId));
                }
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return workers;
    }

    @Override
    public Worker getWorker(final long jobExecutionId, final ExecutableId executableId) {
        final Worker worker = getLocalWorker(jobExecutionId, executableId);
        if (worker != null) {
            return worker;
        }
        final List<Address> members = rpc.getMembers();
        final List<Future<WorkerIdAndAddress<Address>>> futures = new ArrayList<>();
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindWorkerForExecutionCallable(jobExecutionId, executableId)));
        }
        for (final Future<WorkerIdAndAddress<Address>> future : futures) {
            try {
                //TODO Search these for completes rather that .get(...) them in order
                final WorkerIdAndAddress<Address> that = future.get(this.timeout, this.unit);
                if (that == null) {
                    continue;
                }
                final Worker rpcWorker;
                if (that.getAddress().equals(local)) {
                    throw new IllegalStateException(); //Also should not have been distributed
                } else {
                    rpcWorker = new InfinispanWorker(this, local, that.getAddress(), that.getWorkerId());
                    remoteWorkers.put(that.getWorkerId(), rpcWorker);
                }
                return rpcWorker;
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO message
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        final ExecutionRepository ours = getRegistry().getExecutionRepository(id);
        if (ours != null) {
            return ours;
        }
        final List<Address> members = new ArrayList<>(rpc.getMembers());
        members.remove(this.local);
        final List<Future<Address>> futures = new ArrayList<>(members.size());
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
                return new InfinispanProxyExecutionRepository(this, id, address);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public Executable getExecutable(final long jobExecutionId, final ExecutableId id) {
        final Executable ours = super.getExecutable(jobExecutionId, id);
        if (ours != null) {
            return ours;
        }
        final List<Address> members = new ArrayList<>(rpc.getMembers());
        members.remove(this.local);
        final List<Future<Executable>> futures = new ArrayList<>(members.size());
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindExecutableCallable(jobExecutionId, id)));
        }
        for (final Future<Executable> future : futures) {
            try {
                final Executable executable = future.get(this.timeout, this.unit);
                if (executable == null) {
                    continue;
                }
                return executable;
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    //TODO
    protected List<Address> filterMembers(final List<Address> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    public Address getAddress() {
        return local;
    }

    @Override
    public <T> void invokeRemote(final Address address, final Command<T, Address> command, final Deferred<T,Throwable,?> promise, final long timeout, final TimeUnit unit) {
        if (address == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        rpc.invokeRemotelyInFuture(
                Collections.singleton(address),
                new CommandAdapter(cacheName, command, getAddress()),
                new RpcOptionsBuilder(options).timeout(timeout, unit).build(),
                new InfinispanFuture<>(this, promise, address, System.currentTimeMillis())
        );
    }
}
