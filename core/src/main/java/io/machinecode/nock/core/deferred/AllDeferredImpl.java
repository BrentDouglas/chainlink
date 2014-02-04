package io.machinecode.nock.core.deferred;

import io.machinecode.nock.spi.deferred.Deferred;

import java.util.concurrent.TimeUnit;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllDeferredImpl<T, U extends Throwable> extends DeferredImpl<T, U> {

    public AllDeferredImpl(final Deferred<?>... chain) {
        super(chain);
        for (final Deferred<?> that : chain) {
            that.onResolve(new Notify(this));
        }
        this.state = RESOLVED;
    }

    @Override
    public boolean isDone() {
        if (this.isCancelled()) {
            return true;
        }
        for (final Deferred<?> that : chain) {
            if (!that.isDone()) {
                return false;
            }
        }
        return true;
    }

    protected void await() throws InterruptedException {
        while (!isDone()) {
            wait();
        }
    }

    protected void await(final long timeout, final TimeUnit unit) throws InterruptedException {
        long start = System.currentTimeMillis();
        long wait = unit.toMillis(timeout);
        while (!isDone()) {
            if (start + wait >= System.currentTimeMillis()) {
                return;
            }
            wait(wait);
        }
    }
}
