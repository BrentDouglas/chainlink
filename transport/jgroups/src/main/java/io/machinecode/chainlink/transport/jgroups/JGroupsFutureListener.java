package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.then.api.Deferred;
import io.machinecode.then.core.FutureDeferred;
import org.jgroups.util.FutureListener;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsFutureListener<T> implements FutureListener<T> {

    final Executor network;
    final Deferred<T, Throwable, ?> promise;
    final long timeout;
    final TimeUnit unit;

    public JGroupsFutureListener(final Executor network, final Deferred<T, Throwable, ?> promise, final long timeout, final TimeUnit unit) {
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
                .onCancel(promise);
        network.execute(run.asRunnable());
    }
}
