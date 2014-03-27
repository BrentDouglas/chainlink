package io.machinecode.chainlink.spi.deferred;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Deferred<T> extends Future<T> {

    void resolve(final T that);

    void reject(final Throwable that);

    void link(final Deferred<?> that);

    boolean isResolved();

    boolean isRejected();

    Throwable getFailure() throws InterruptedException, ExecutionException;

    Throwable getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

    void always(final Listener listener);

    void onResolve(final Listener listener);

    void onReject(final Listener listener);

    void onCancel(final Listener listener);

    void onLink(final Listener listener);

    void await(final Lock lock, final Condition condition) throws InterruptedException, ExecutionException;

    void await(final long timeout, final TimeUnit unit, final Lock lock, final Condition condition) throws InterruptedException, ExecutionException, TimeoutException;

    void signal();
}
