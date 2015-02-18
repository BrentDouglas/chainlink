package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.core.transport.DistributedTransport;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.DeferredImpl;
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
public class InfinispanTransport extends DistributedTransport<Address> {

    private static final Logger log = Logger.getLogger(InfinispanTransport.class);

    private static final Configuration PLACEHOLDER = new DummyConfiguration();

    final EmbeddedCacheManager manager;
    RpcManager rpc;
    final Address local;
    final String cacheName;
    AdvancedCache<?, ?> cache;
    final RpcOptions options;
    DistributedExecutorService distributor;
    final Executor network;
    final Executor reaper;

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
    }

    @Override
    public void open(final Configuration configuration) throws Exception {
        super.open(configuration);
        final GlobalComponentRegistry gcr = manager.getGlobalComponentRegistry();
        final Configuration existing = gcr.getComponent(Configuration.class);
        if (existing != null && existing != PLACEHOLDER) {
            throw new IllegalStateException("A transport is already configured for this cache manager."); //TODO Message
        }
        gcr.registerComponent(configuration, Configuration.class); //TODO One Registry per config, this will be an issue
        doOpen();
    }

    protected void doOpen() throws Exception {
        this.cache = manager.getCache(this.cacheName, true).getAdvancedCache();
        this.rpc = cache.getRpcManager();
        this.distributor = new DefaultExecutorService(cache);
        if (!manager.isRunning(this.cacheName)) {
            manager.startCaches(this.cacheName);
        }
        //TODO Add timeout
        while (!manager.isRunning(this.cacheName)) {
            Thread.sleep(100);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
        manager.getGlobalComponentRegistry().registerComponent(PLACEHOLDER, Configuration.class);
    }

    @Override
    public Address getAddress() {
        return local;
    }

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
    public <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command) {
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
