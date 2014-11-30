package io.machinecode.chainlink.test.core.execution.chunk.artifact.listener;

import io.machinecode.chainlink.test.core.execution.chunk.artifact.ChunkEvent;

import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.api.chunk.listener.ItemProcessListener;
import javax.batch.api.chunk.listener.ItemReadListener;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.api.chunk.listener.RetryProcessListener;
import javax.batch.api.chunk.listener.RetryReadListener;
import javax.batch.api.chunk.listener.RetryWriteListener;
import javax.batch.api.chunk.listener.SkipProcessListener;
import javax.batch.api.chunk.listener.SkipReadListener;
import javax.batch.api.chunk.listener.SkipWriteListener;
import javax.batch.api.listener.JobListener;
import javax.batch.api.listener.StepListener;
import java.util.List;

import static io.machinecode.chainlink.test.core.execution.chunk.artifact.EventOrderAccumulator._order;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventOrderListener implements JobListener, StepListener, ChunkListener, ItemReadListener, ItemWriteListener,
                                           ItemProcessListener , RetryReadListener, RetryWriteListener, RetryProcessListener,
                                           SkipReadListener, SkipWriteListener, SkipProcessListener {

    @Override
    public void beforeChunk() throws Exception {
        _order.add(ChunkEvent.BEFORE_CHUNK);
    }

    @Override
    public void onError(final Exception exception) throws Exception {
        _order.add(ChunkEvent.ON_CHUNK_ERROR);
    }

    @Override
    public void afterChunk() throws Exception {
        _order.add(ChunkEvent.AFTER_CHUNK);
    }

    @Override
    public void beforeStep() throws Exception {
        _order.add(ChunkEvent.BEFORE_STEP);
    }

    @Override
    public void afterStep() throws Exception {
        _order.add(ChunkEvent.AFTER_STEP);
    }

    @Override
    public void beforeJob() throws Exception {
        _order.add(ChunkEvent.BEFORE_JOB);
    }

    @Override
    public void afterJob() throws Exception {
        _order.add(ChunkEvent.AFTER_JOB);
    }

    @Override
    public void beforeProcess(final Object item) throws Exception {
        _order.add(ChunkEvent.BEFORE_PROCESS);
    }

    @Override
    public void afterProcess(final Object item, final Object result) throws Exception {
        _order.add(ChunkEvent.AFTER_PROCESS);
    }

    @Override
    public void onProcessError(final Object item, final Exception exception) throws Exception {
        _order.add(ChunkEvent.ON_PROCESS_ERROR);
    }

    @Override
    public void beforeRead() throws Exception {
        _order.add(ChunkEvent.BEFORE_READ);
    }

    @Override
    public void afterRead(final Object item) throws Exception {
        _order.add(ChunkEvent.AFTER_READ);
    }

    @Override
    public void onReadError(final Exception exception) throws Exception {
        _order.add(ChunkEvent.ON_READ_ERROR);
    }

    @Override
    public void beforeWrite(final List<Object> items) throws Exception {
        _order.add(ChunkEvent.BEFORE_WRITE);
    }

    @Override
    public void afterWrite(final List<Object> items) throws Exception {
        _order.add(ChunkEvent.AFTER_WRITE);
    }

    @Override
    public void onWriteError(final List<Object> items, final Exception exception) throws Exception {
        _order.add(ChunkEvent.ON_WRITE_ERROR);
    }

    @Override
    public void onRetryProcessException(final Object item, final Exception exception) throws Exception {
        _order.add(ChunkEvent.RETRY_PROCESS_EXCEPTION);
    }

    @Override
    public void onRetryReadException(final Exception exception) throws Exception {
        _order.add(ChunkEvent.RETRY_READ_EXCEPTION);
    }

    @Override
    public void onRetryWriteException(final List<Object> items, final Exception exception) throws Exception {
        _order.add(ChunkEvent.RETRY_WRITE_EXCEPTION);
    }

    @Override
    public void onSkipProcessItem(final Object item, final Exception exception) throws Exception {
        _order.add(ChunkEvent.SKIP_PROCESS);
    }

    @Override
    public void onSkipReadItem(final Exception exception) throws Exception {
        _order.add(ChunkEvent.SKIP_READ);
    }

    @Override
    public void onSkipWriteItem(final List<Object> items, final Exception exception) throws Exception {
        _order.add(ChunkEvent.SKIP_WRITE);
    }
}
