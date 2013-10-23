package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.work.Deferred;

import java.util.concurrent.TimeUnit;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllDeferredImpl<T> extends DeferredImpl<T> {

    public AllDeferredImpl(final Deferred<?>... chain) {
        super(chain);
        for (final Deferred<?> that : chain) {
            that.addListener(new Notify(this));
        }
        this.done = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        if (cancelled) {
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
