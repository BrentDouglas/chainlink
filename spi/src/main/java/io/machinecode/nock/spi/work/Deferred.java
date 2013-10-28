package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.transport.Synchronization;

import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Deferred<T> extends Future<T>, Synchronization {

    void resolve(final T that);

    void onResolve(final Listener listener);

    void onCancel(final Listener listener);

    void always(final Listener listener);
}
