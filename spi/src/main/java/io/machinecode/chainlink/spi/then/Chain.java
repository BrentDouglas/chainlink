package io.machinecode.chainlink.spi.then;

import io.machinecode.then.api.Deferred;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Chain<T> extends Deferred<T,Throwable,Void> {

    Chain<T> link(final Chain<?> that);

    Chain<T> previous(final Chain<?> that);

    Chain<T> onLink(final OnLink then);

    void awaitLink() throws InterruptedException, ExecutionException;

    void awaitLink(final long timeout, final TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException;

    void notifyLinked();
}
