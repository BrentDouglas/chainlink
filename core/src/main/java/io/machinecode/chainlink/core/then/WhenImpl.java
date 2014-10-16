package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.When;
import io.machinecode.then.api.Deferred;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WhenImpl implements When {

    private final ExecutorService executor;

    public WhenImpl(final ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public <T> void when(final Future<T> future, final Deferred<T,Throwable,?> then) {
        this.executor.submit(new Runnable() {
            @Override
            public void run() {
                T that = null;
                try {
                    that = future.get();
                } catch (final CancellationException e) {
                    then.cancel(true);
                } catch (final Throwable e) {
                    then.reject(e);
                }
                then.resolve(that);
            }
        });
    }

    @Override
    public <T> void when(final long timeout, final TimeUnit unit, final Future<T> future, final Deferred<T,Throwable,?> then) {
        this.executor.submit(new Runnable() {
            @Override
            public void run() {
                T that = null;
                try {
                    that = future.get(timeout, unit);
                } catch (final CancellationException e) {
                    then.cancel(true);
                } catch (final Throwable e) {
                    then.reject(e);
                }
                then.resolve(that);
            }
        });
    }

    @Override
    public void startup() {
        //no op
    }

    @Override
    public void shutdown() {
        this.executor.shutdown();
    }
}
