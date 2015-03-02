package io.machinecode.chainlink.core.execution.artifact.reader;

import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.exception.FailReadCloseException;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailCloseAlwaysEventOrderReader implements ItemReader {

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CLOSE);
        throw new FailReadCloseException();
    }

    @Override
    public Object readItem() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READ);
        return new Object();
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CHECKPOINT);
        return null;
    }
}
