package io.machinecode.chainlink.spi.then;

import io.machinecode.then.api.Deferred;
import io.machinecode.then.api.Promise;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Chain<T> extends Deferred<T> {

    Chain<T> link(final Chain<?> that);

    Chain<T> previous(final Chain<?> that);

    Chain<T> onLink(final OnLink then);

    Promise<Void> await();

    void notifyLinked();
}
