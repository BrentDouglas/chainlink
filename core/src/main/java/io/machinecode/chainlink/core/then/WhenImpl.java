package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.then.When;
import io.machinecode.then.api.Promise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WhenImpl implements When {

    private final ExecutorService when = Executors.newSingleThreadExecutor();

    @Override
    public <T> void when(final Future<T> future, final Promise<T,Throwable> then) {
        this.when.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    then.resolve(future.get());
                } catch (final Throwable e) {
                    then.reject(e);
                }
            }
        });
    }

    @Override
    public <T> void when(final long timeout, final TimeUnit unit, final Future<T> future, final Promise<T,Throwable> then) {
        this.when.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    then.resolve(future.get(timeout, unit));
                } catch (final Throwable e) {
                    then.reject(e);
                }
            }
        });
    }

    @Override
    public void startup() {
        //no op
    }

    @Override
    public void shutdown() {
        this.when.shutdown();
    }
}
