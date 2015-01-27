package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.transport.cmd.Command;
import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnCancel;
import io.machinecode.then.api.OnReject;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.core.FutureDeferred;
import org.jboss.logging.Logger;
import org.jgroups.Address;
import org.jgroups.util.FutureListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsFutureListener<T> implements FutureListener<T>, OnReject<Throwable>, OnResolve<T>, OnCancel {
    private static final Logger log = Logger.getLogger(JGroupsFutureListener.class);

    final Address address;
    final Command<T> command;

    final Executor network;
    final Deferred<T, Throwable, ?> promise;
    final long timeout;
    final TimeUnit unit;

    public JGroupsFutureListener(final Address address, final Command<T> command, final Executor network,
                                 final Deferred<T, Throwable, ?> promise, final long timeout, final TimeUnit unit) {
        this.address = address;
        this.command = command;
        this.network = network;
        this.promise = promise;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void futureDone(final Future<T> future) {
        final FutureDeferred<T, Void> run = new FutureDeferred<>(future, timeout, unit);
        run.onResolve(promise)
                .onReject(promise)
                .onCancel(promise)
                .onResolve(this)
                .onReject(this)
                .onCancel(this);
        network.execute(run.asRunnable());
    }

    @Override
    public void reject(final Throwable fail) {
        log.tracef("Received reject from %s: %s %s.", address, command, fail);
    }

    @Override
    public void resolve(final T that) {
        log.tracef("Received resolve from %s: %s %s.", address, command, that);
    }

    @Override
    public boolean cancel(final boolean mayInterrupt) {
        log.tracef("Received cancel from %s: %s.", address, command);
        return false;
    }
}
