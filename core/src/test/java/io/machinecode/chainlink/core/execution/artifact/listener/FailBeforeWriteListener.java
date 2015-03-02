package io.machinecode.chainlink.core.execution.artifact.listener;

import io.machinecode.chainlink.core.execution.artifact.exception.FailListenException;

import javax.batch.api.chunk.listener.ItemWriteListener;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailBeforeWriteListener implements ItemWriteListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void beforeWrite(final List<Object> items) throws Exception {
        count.incrementAndGet();
        throw new FailListenException();
    }

    @Override
    public void afterWrite(final List<Object> items) throws Exception {
        //
    }

    @Override
    public void onWriteError(final List<Object> items, final Exception exception) throws Exception {
        //
    }
}
