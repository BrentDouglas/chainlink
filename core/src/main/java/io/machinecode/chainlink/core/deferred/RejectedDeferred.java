package io.machinecode.chainlink.core.deferred;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;
import io.machinecode.chainlink.spi.deferred.ResolvedException;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RejectedDeferred<T> implements Deferred<T> {

    final Throwable that;

    public RejectedDeferred(final Throwable that) {
        this.that = that;
    }

    @Override
    public void resolve(final T that) {
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public void reject(final Throwable that) {
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public boolean isResolved() {
        return false;
    }

    @Override
    public boolean isRejected() {
        return true;
    }

    @Override
    public Throwable getFailure() throws InterruptedException, ExecutionException {
        return that;
    }

    @Override
    public Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return that;
    }

    @Override
    public void always(final Listener listener) {
        listener.run(this);
    }

    @Override
    public void onResolve(final Listener listener) {
        //no-op
    }

    @Override
    public void onReject(final Listener listener) {
        listener.run(this);
    }

    @Override
    public void onCancel(final Listener listener) {
        //no-op
    }

    @Override
    public void traverse(final Listener listener) {
        //no-op
    }

    @Override
    public void await() throws InterruptedException, ExecutionException {
        //no-op
    }

    @Override
    public void await(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        //no-op
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), that);
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        throw new ExecutionException(Messages.format("CHAINLINK-004001.deferred.rejected"), that);
    }
}
