package io.machinecode.chainlink.spi.then;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.then.api.Deferred;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface When extends Lifecycle {

    <T> void when(final Future<T> future, final Deferred<T,Throwable,?> then);

    <T> void when(final long timeout, final TimeUnit unit, final Future<T> future, final Deferred<T,Throwable,?> then);
}
