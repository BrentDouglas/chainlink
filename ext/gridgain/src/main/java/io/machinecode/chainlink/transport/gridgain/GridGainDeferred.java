package io.machinecode.chainlink.transport.gridgain;

import io.machinecode.then.core.DeferredImpl;
import org.gridgain.grid.GridFuture;
import org.gridgain.grid.GridFutureCancelledException;
import org.gridgain.grid.util.typedef.CI1;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GridGainDeferred<T> extends DeferredImpl<T,Throwable,Object> implements CI1<GridFuture<T>> {
    private static final long serialVersionUID = 1L;

    final long timeout;
    final TimeUnit unit;

    public GridGainDeferred(final long timeout, final TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    @Override
    public void apply(final GridFuture<T> future) {
        final T that;
        try {
            that = future.get(timeout, unit);
        } catch (final GridFutureCancelledException e) {
            this.cancel(true);
            return;
        } catch (final Throwable e) {
            this.reject(e);
            return;
        }
        this.resolve(that);
    }
}
