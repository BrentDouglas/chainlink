package io.machinecode.nock.spi.deferred;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Deferred<T> extends Future<T> {

    void resolve(final T that);

    void reject(final Throwable that);

    boolean isResolved();

    boolean isRejected();

    Throwable getFailure() throws InterruptedException, ExecutionException;

    Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

    void onResolve(final Listener listener);

    void onReject(final Listener listener);

    void onCancel(final Listener listener);

    void traverse(final Listener listener);

    void await() throws InterruptedException, ExecutionException;

    void await(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
}
