package io.machinecode.chainlink.core.transport;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.transport.cmd.CleanupCommand;
import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.JobEventListener;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.util.Pair;
import io.machinecode.then.api.OnComplete;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.FutureDeferred;
import io.machinecode.then.core.SomeDeferred;
import org.jboss.logging.Logger;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class DistributedTransport<A> extends BaseTransport<A> {

    private static final Logger log = Logger.getLogger(DistributedTransport.class);

    protected final WeakReference<ClassLoader> loader;
    protected final Marshalling marshalling;
    protected final Executor network;
    protected final Executor reaper;

    final TLongObjectMap<List<Pair<ChainId,A>>> remoteExecutions = new TLongObjectHashMap<>();

    protected final long timeout;
    protected final TimeUnit unit;

    public DistributedTransport(final Dependencies dependencies, final Properties properties) throws Exception {
        super(dependencies, properties);
        this.loader = new WeakReference<>(dependencies.getClassLoader());
        this.marshalling = dependencies.getMarshalling();

        this.network= Executors.newCachedThreadPool();
        this.reaper = Executors.newSingleThreadExecutor();

        this.timeout = Long.parseLong(properties.getProperty(Constants.TIMEOUT, Constants.Defaults.NETWORK_TIMEOUT));
        this.unit = TimeUnit.valueOf(properties.getProperty(Constants.TIMEOUT_UNIT, Constants.Defaults.NETWORK_TIMEOUT_UNIT));

        this.registry.registerJobEventListener("cleanup-remote-jobs", new JobEventListener() {
            @Override
            public Promise<?,?,?> onRegister(final long jobExecutionId, final Chain<?> job) {
                remoteExecutions.put(jobExecutionId, new ArrayList<Pair<ChainId, A>>());
                return null;
            }

            @Override
            public Promise<?,?,?> onUnregister(final long jobExecutionId, final Chain<?> job) {
                final CleanupCommand command = new CleanupCommand(jobExecutionId);
                final FutureDeferred<Object, Void> promise = new FutureDeferred<>(job, timeout, unit);
                promise.onComplete(new OnComplete() {
                    @Override
                    public void complete(final int status) {
                        for (final Pair<ChainId, A> pair : remoteExecutions.remove(jobExecutionId)) {
                            final A address = pair.getValue();
                            if (!address.equals(getAddress())) {
                                try {
                                    _invoke(address, command);
                                } catch (Exception e) {
                                    log.errorf(e, ""); // TODO Message
                                }
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

    public abstract A getAddress();

    @Override
    public void close() throws Exception {
        this.registry.unregisterJobEventListener("cleanup-remote-jobs");
        super.close();
    }

    protected abstract List<A> getRemotes();

    @Override
    protected <T> Promise<? extends Iterable<T>,Throwable,Object> invokeEverywhere(final Command<T> command, final long timeout, final TimeUnit unit) {
        final List<A> remotes = getRemotes();
        final List<Promise<T,Throwable,?>> promises = new ArrayList<>(remotes.size());
        for (final A remote : remotes) {
            promises.add(invokeRemote(remote, command, timeout, unit));
        }
        return new SomeDeferred<>(promises);
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return unit;
    }

    protected <T> Future<T> _invoke(final A address, final Command<T> command) throws Exception {
        return invokeRemote(address, command, this.timeout, this.unit);
    }

    protected List<A> _remoteMembers(final Collection<A> all) {
        final List<A> that = new ArrayList<>(all);
        that.remove(this.getAddress());
        return Collections.unmodifiableList(that);
    }
}
