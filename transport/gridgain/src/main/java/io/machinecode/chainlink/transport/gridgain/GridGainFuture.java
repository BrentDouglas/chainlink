package io.machinecode.chainlink.transport.gridgain;

import org.gridgain.grid.GridException;
import org.gridgain.grid.GridFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class GridGainFuture<T> implements Future<T> {

    final GridFuture<T> future;

    public GridGainFuture(final GridFuture<T> future) {
        this.future = future;
    }
    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        try {
            return future.cancel();
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return future.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        try {
            return future.get();
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            return future.get(timeout, unit);
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }
}
