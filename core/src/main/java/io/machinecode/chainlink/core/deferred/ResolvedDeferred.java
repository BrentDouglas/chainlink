package io.machinecode.chainlink.core.deferred;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolvedDeferred<T> extends BaseDeferred<T> {

    public ResolvedDeferred(final T value) {
        resolve(value);
    }

    @Override
    public void link(final Deferred<?> that) {
        //
    }

    @Override
    public void onLink(final Listener listener) {
        //
    }

    @Override
    public void await(final Lock lock, final Condition condition) throws InterruptedException {
        //
    }

    @Override
    public void await(final long timeout, final TimeUnit unit, final Lock lock, final Condition condition) throws InterruptedException, TimeoutException {
        //
    }
}
