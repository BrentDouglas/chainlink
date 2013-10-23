package io.machinecode.nock.spi.work;

import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Deferred<T> extends Future<T> {

    void resolve(final T that);

    void addListener(final Listener listener);
}
