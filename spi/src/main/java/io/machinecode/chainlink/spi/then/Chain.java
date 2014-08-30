package io.machinecode.chainlink.spi.then;

import io.machinecode.then.api.Promise;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Chain<T> extends Promise<T,Throwable> {

    Chain<T> link(final Chain<?> that);

    Chain<T> previous(final Chain<?> that);

    Chain<T> onLink(final OnLink then);

    Promise<Void,Throwable> awaitLink();

    void notifyLinked();
}
