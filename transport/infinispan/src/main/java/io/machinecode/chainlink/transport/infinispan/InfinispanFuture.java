package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.OnResolve;
import io.machinecode.then.core.DeferredImpl;
import org.infinispan.commons.util.concurrent.FutureListener;
import org.infinispan.commons.util.concurrent.NotifyingNotifiableFuture;
import org.infinispan.remoting.responses.ExceptionResponse;
import org.infinispan.remoting.responses.Response;
import org.infinispan.remoting.responses.SuccessfulResponse;
import org.infinispan.remoting.transport.Address;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class InfinispanFuture<T,U> implements NotifyingNotifiableFuture<T> {

    private final InfinispanRegistry registry;
    private final Deferred<U,Throwable,?> promise;
    private final Address address;
    private final long start;
    private Future<T> io;

    public InfinispanFuture(final InfinispanRegistry registry, final Deferred<U,Throwable,?> promise, final Address address, final long start) {
        this.registry = registry;
        this.promise = promise;
        this.address = address;
        this.start = start;
    }

    @Override
    public void notifyDone() {
        registry.network.when(
                System.currentTimeMillis() - start + registry.options.timeUnit().toMillis(registry.options.timeout()),
                TimeUnit.MILLISECONDS,
                io,
                new DeferredImpl<T, Throwable, Void>().onResolve(new OnResolve<T>() {
                    @Override
                    public void resolve(final T ret) {
                        try {
                            if (ret == null || !(ret instanceof Map)) {
                                promise.reject(new IllegalStateException()); //TODO Message
                                return;
                            }
                            final Response response = ((Map<Address, Response>)ret).get(address);
                            if (response == null) {
                                promise.resolve(null);
                            } else if (response.isSuccessful()) {
                                promise.resolve((U) ((SuccessfulResponse) response).getResponseValue());
                            } else {
                                if (response instanceof ExceptionResponse) {
                                    promise.reject(((ExceptionResponse)response).getException());
                                } else {
                                    promise.reject(new IllegalStateException()); //TODO Message
                                }
                            }
                        } catch (final Throwable e) {
                            promise.reject(e);
                        }
                    }
                }).onReject(promise)
        );
    }

    @Override
    public void setNetworkFuture(final Future<T> future) {
        this.io = future;
    }

    @Override
    public InfinispanFuture<T,U> attachListener(final FutureListener<T> listener) {
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return io.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return io.isCancelled();
    }

    @Override
    public boolean isDone() {
        return io.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return io.get();
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return io.get(timeout, unit);
    }
}
