package io.machinecode.chainlink.core.execution.artifact.listener;

import io.machinecode.chainlink.core.execution.artifact.exception.FailListenException;

import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.listener.JobListener;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailBeforeChunkListener implements ChunkListener {

    public static final AtomicInteger count = new AtomicInteger(0);

    public static void reset() {
        count.set(0);
    }

    public static int get() {
        return count.get();
    }

    @Override
    public void beforeChunk() throws Exception {
        count.incrementAndGet();
        throw new FailListenException();
    }

    @Override
    public void onError(final Exception exception) throws Exception {
        //
    }

    @Override
    public void afterChunk() throws Exception {
        //
    }
}
