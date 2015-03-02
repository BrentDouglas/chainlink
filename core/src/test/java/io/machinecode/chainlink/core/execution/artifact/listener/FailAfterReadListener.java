package io.machinecode.chainlink.core.execution.artifact.listener;

import io.machinecode.chainlink.core.execution.artifact.exception.FailListenException;

import javax.batch.api.chunk.listener.ItemReadListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailAfterReadListener implements ItemReadListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void beforeRead() throws Exception {
        //
    }

    @Override
    public void afterRead(final Object item) throws Exception {
        count.incrementAndGet();
        throw new FailListenException();
    }

    @Override
    public void onReadError(final Exception exception) throws Exception {
        //
    }
}
