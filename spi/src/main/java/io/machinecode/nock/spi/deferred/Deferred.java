package io.machinecode.nock.spi.deferred;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Deferred<T, U extends Throwable> extends Future<T> {

    void resolve(final T that);

    void reject(final U that);

    boolean isResolved();

    boolean isRejected();

    U getFailure() throws InterruptedException, ExecutionException;

    U getFailure(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

    void onResolve(final Listener listener);

    void onReject(final Listener listener);

    void onCancel(final Listener listener);
}
