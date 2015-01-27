package io.machinecode.chainlink.transport.infinispan;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.transport.BaseTransport;
import io.machinecode.chainlink.core.transport.cmd.CleanupCommand;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;
import io.machinecode.then.core.FutureDeferred;
import io.machinecode.then.core.RejectedDeferred;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanTransport extends BaseTransport<Address> {

    private static final Logger log = Logger.getLogger(InfinispanTransport.class);
    public static final String CLEANUP_INFINISPAN = "cleanup-infinispan";

    final EmbeddedCacheManager manager;
    RpcManager rpc;
    final Address local;
    final String cacheName;
    AdvancedCache<Object, Object> cache;
    final RpcOptions options;
    DistributedExecutorService distributor;
    final Executor network;
    final Executor reaper;

    final long timeout;
    final TimeUnit unit;

    final TLongObjectMap<List<Pair<ChainId,Address>>> remoteExecutions = new TLongObjectHashMap<>();

    public InfinispanTransport(final Dependencies configuration, final Properties properties, final EmbeddedCacheManager manager,
                               final long timeout, final TimeUnit unit) throws Exception {
        super(configuration, properties);
        this.manager = manager;
        final GlobalComponentRegistry gcr = manager.getGlobalComponentRegistry();
        if (manager.getStatus() == ComponentStatus.INSTANTIATED) {
            manager.start();
            gcr.start();
        }
        //TODO Add timeout
        while (manager.getStatus().ordinal() < ComponentStatus.RUNNING.ordinal()) {
            Thread.sleep(100);
        }
        this.network = Executors.newSingleThreadExecutor();
        this.reaper = Executors.newSingleThreadExecutor();
        this.local = manager.getAddress();
        this.cacheName = properties.getProperty(InfinispanConstants.CACHE, InfinispanConstants.CACHE);
        this.options = new RpcOptions(
                timeout,
                unit,
                null,
                ResponseMode.SYNCHRONOUS,
                false,
                true,
                false
        );
        this.timeout = Long.parseLong(properties.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(properties.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));

    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        super.open(configuration);
        final GlobalComponentRegistry gcr = manager.getGlobalComponentRegistry();
        gcr.registerComponent(configuration, Configuration.class); //TODO One Registry per config, this will be an issue
        //TODO Have to register configuration before this
        this.cache = manager.<Object, Object>getCache(this.cacheName, true).getAdvancedCache();
        this.rpc = cache.getRpcManager();
        this.distributor = new DefaultExecutorService(cache);
        if (!manager.isRunning(this.cacheName)) {
            manager.startCaches(this.cacheName);
        }
        //TODO Add timeout
        while (!manager.isRunning(this.cacheName)) {
            Thread.sleep(100);
        }
        this.registry.registerJobEventListener(CLEANUP_INFINISPAN, new JobEventListener() {
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
                                        new CommandAdapter(cacheName, new CleanupCommand(jobExecutionId), local),
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
        this.registry.unregisterJobEventListener(CLEANUP_INFINISPAN);
        super.close();
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
    public Address getAddress() {
        return local;
    }

    @Override
    public <T> Promise<T,Throwable,Object> invokeRemote(final Object address, final Command<T> command, final long timeout, final TimeUnit unit) {
        if (!(address instanceof Address)) {
            return new RejectedDeferred<T, Throwable, Object>(new IllegalArgumentException()); //TODO Message
        }
        final Address addr = (Address)address;
        final DeferredImpl<T,Throwable,Object> deferred = new DeferredImpl<>();
        rpc.invokeRemotelyInFuture(
                Collections.singleton(addr),
                new CommandAdapter(cacheName, command, getAddress()),
                new RpcOptionsBuilder(options).timeout(timeout, unit).build(),
                new InfinispanFuture<>(this, deferred, addr, System.currentTimeMillis())
        );
        return deferred;
    }

    @Override
    public <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command, final long timeout, final TimeUnit unit) {
        final List<Address> remotes = rpc.getMembers();
        final DeferredImpl<Iterable<T>,Throwable,Object> deferred = new DeferredImpl<>();
        rpc.invokeRemotelyInFuture(
                remotes,
                new CommandAdapter(cacheName, command, getAddress()),
                new RpcOptionsBuilder(options).timeout(timeout, unit).build(),
                new InfinispanMultiFuture<>(this, deferred, remotes, System.currentTimeMillis())
        );
        return deferred;
    }
}
