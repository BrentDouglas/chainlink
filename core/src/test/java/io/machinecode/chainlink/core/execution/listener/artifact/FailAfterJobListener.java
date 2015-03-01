package io.machinecode.chainlink.core.execution.listener.artifact;

import javax.batch.api.listener.JobListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailAfterJobListener implements JobListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void beforeJob() throws Exception {
        //
    }

    @Override
    public void afterJob() throws Exception {
        count.incrementAndGet();
        throw new FailListenException();
    }
}
