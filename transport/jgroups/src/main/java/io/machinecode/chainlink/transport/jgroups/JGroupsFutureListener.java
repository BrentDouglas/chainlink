package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.spi.then.When;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.api.Promise;
import io.machinecode.then.core.PromiseImpl;
import org.jgroups.util.FutureListener;

import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsFutureListener<T> implements FutureListener<T> {

    final When network;
    final Promise<T,Throwable> promise;
    final long timeout;
    final TimeUnit unit;

    public JGroupsFutureListener(final When network, final Promise<T, Throwable> promise, final long timeout, final TimeUnit unit) {
        this.network = network;
        this.promise = promise;
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void futureDone(final Future<T> future) {
        network.when(
                timeout, unit,
                future,
                new PromiseImpl<T,Throwable>().onResolve(new OnResolve<T>() {
                    @Override
                    public void resolve(final T ret) {
                        try {
                            promise.resolve(ret);
                        } catch (final Throwable e) {
                            promise.reject(e);
                        }
                    }
                }).onReject(promise)
        );
    }
}
