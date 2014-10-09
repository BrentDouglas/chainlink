package io.machinecode.chainlink.spi.then;

import io.machinecode.then.api.Deferred;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Chain<T> extends Deferred<T,Throwable,Void> {

    void link(final Chain<?> link);

    void linkAndResolve(final T value, final Chain<?> link);

    void linkAndReject(final Throwable failure, final Chain<?> link);

    void previous(final Chain<?> that);

    Chain<T> onLink(final OnLink then);

    void awaitLink() throws InterruptedException, ExecutionException;

    void awaitLink(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException;

    void notifyLinked();
}
