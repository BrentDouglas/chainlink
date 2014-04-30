package io.machinecode.chainlink.transport.infinispan;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.When;
import io.machinecode.chainlink.transport.infinispan.callable.FindExecutionRepositoryWithIdCallable;
import io.machinecode.chainlink.transport.infinispan.callable.FindWorkerCallable;
import io.machinecode.chainlink.transport.infinispan.callable.LeastBusyWorkerCallable;
import io.machinecode.chainlink.transport.infinispan.cmd.CleanupCommand;
import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.JobRegistry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.On;
import io.machinecode.then.api.Promise;
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
import org.infinispan.remoting.transport.Address;
import org.jboss.logging.Logger;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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
    final Timer timer;
    final When when;

    final TMap<WorkerId, Worker> remoteWorkers = new THashMap<WorkerId, Worker>();
    final TLongObjectMap<List<Pair<ChainId,Address>>> remoteExecutions = new TLongObjectHashMap<List<Pair<ChainId,Address>>>();

    public InfinispanRegistry(final RegistryConfiguration configuration, final EmbeddedCacheManager manager,
                              final long timeout, final TimeUnit unit) {
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
        this.when = configuration.getWhen();
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
        this.timer = new Timer(Messages.get("CHAINLINK-005200.registry.eviction.timer"), true);
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
        onRegisterJob(new On<Long>() {
            @Override
            public void on(final Long jobExecutionId) {
                remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, Address>>());
            }
        });
        onUnregisterJob(new On<Long>() {
            @Override
            public void on(final Long jobExecutionId) {
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
            }
        });
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
            return new InfinispanThreadId((Thread)worker, local);
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
        if (workerId instanceof InfinispanThreadId) {
            remote = ((InfinispanThreadId) workerId).address;
        }
        if (remote != null) {
            if (remote.equals(local)) {
                throw new IllegalStateException(); //This should have been handled at the start
            }
            final Worker rpcWorker = new RemoteWorker(this, local, remote, workerId);
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
                final Address address = future.get();
                if (address == null) {
                    continue;
                }
                final Worker rpcWorker;
                if (address.equals(local)) {
                    throw new IllegalStateException(); //Also should not have been distributed
                } else {
                    rpcWorker = new RemoteWorker(this, local, address, workerId);
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

    private List<Worker> _localWorkers(final int required) {
        return super.getWorkers(required);
    }

    @Override
    public List<Worker> getWorkers(final int required) {
        final List<Address> members = rpc.getMembers();
        final List<Future<InfinispanThreadId>> futures = new ArrayList<Future<InfinispanThreadId>>(required);
        for (final Address address : filterMembers(members, required)) {
            futures.add(distributor.submit(address, new LeastBusyWorkerCallable()));
        }
        final ArrayList<Worker> workers = new ArrayList<Worker>(required);
        for (final Future<InfinispanThreadId> future : futures) {
            try {
                final InfinispanThreadId threadId = future.get();
                if (local.equals(threadId.address)) {
                    workers.add(getWorker(threadId));
                } else {
                    workers.add(new RemoteWorker(this, local, threadId.address, threadId));
                }
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
    public InfinispanJobRegistry getJobRegistry(final long jobExecutionId) throws JobExecutionNotRunningException {
        try {
            return (InfinispanJobRegistry)super.getJobRegistry(jobExecutionId);
        } catch (final JobExecutionNotRunningException e) {
            //
        }
        final InfinispanJobRegistry registry = new InfinispanJobRegistry(this, jobExecutionId);
        while (!jobLock.compareAndSet(false, true)) {}
        try {
            this.jobRegistries.put(jobExecutionId, registry);
        } finally {
            jobLock.set(false);
        }
        return registry;
    }

    @Override
    public ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) {
        final ExecutionRepository repository = super.getExecutionRepository(id);
        if (repository != null) {
            return repository;
        }
        final List<Address> members = rpc.getMembers();
        final List<Future<Address>> futures = new ArrayList<Future<Address>>(members.size());
        for (final Address address : members) {
            futures.add(distributor.submit(address, new FindExecutionRepositoryWithIdCallable(id)));
        }
        for (final Future<Address> future : futures) {
            try {
                final Address address = future.get();
                if (address == null) {
                    continue;
                } else if (local.equals(address)) {
                    throw new IllegalStateException(); //TODO Message
                }
                return new RemoteExecutionRepository(this, id, address);
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

    public InfinispanJobRegistry getLocalJobRegistry(final long jobExecutionId) throws JobExecutionNotRunningException {
        return (InfinispanJobRegistry)super.getJobRegistry(jobExecutionId);
    }

    @Override
    protected JobRegistry _createJobRegistry(final long jobExecutionId) {
        return new InfinispanJobRegistry(this, jobExecutionId);
    }

    //TODO
    protected List<Address> filterMembers(final List<Address> all, final int required) {
        return all.subList(0, required > all.size() ? all.size() : required);
    }

    public InfinispanThreadId leastBusyWorker() {
        return (InfinispanThreadId)getWorker().id();
    }

    public Address getLocal() {
        return local;
    }

    public boolean hasWorker(final WorkerId workerId) {
        return _localWorker(workerId) != null;
    }

    public <T> void invoke(final Address address, final ReplicableCommand command, final Promise<T> promise) {
        rpc.invokeRemotelyInFuture(
                Collections.singleton(address),
                command,
                options,
                new InfinispanFuture<Object,T>(this, promise, address, System.currentTimeMillis())
        );
    }
}
