package io.machinecode.chainlink.core.execution.artifact.listener;

import io.machinecode.chainlink.core.execution.artifact.exception.FailListenException;

import javax.batch.api.chunk.listener.RetryReadListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailRetryReadListener implements RetryReadListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void onRetryReadException(final Exception exception) throws Exception {
        count.incrementAndGet();
        throw new FailListenException();
    }
}
