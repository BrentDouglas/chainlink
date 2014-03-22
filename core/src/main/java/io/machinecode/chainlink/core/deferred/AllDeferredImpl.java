package io.machinecode.chainlink.core.deferred;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.deferred.Listener;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllDeferredImpl<T> extends DeferredImpl<T> {

    public AllDeferredImpl(final Deferred<?>... chain) {
        super(chain);
        final Listener notify = new Listener() {
            @Override
            public void run(final Deferred<?> deferred) {
                _notifyAll(); //TODO Why
            }
        };
        for (final Deferred<?> that : chain) {
            that.always(notify);
        }
        resolve(null);
    }

    @Override
    public boolean isDone() {
        boolean done = true;
        for (final Deferred<?> that : children) {
            if (that == null) {
                continue;
            }
            done = that.isDone() && done;
        }
        return done;
    }
}
