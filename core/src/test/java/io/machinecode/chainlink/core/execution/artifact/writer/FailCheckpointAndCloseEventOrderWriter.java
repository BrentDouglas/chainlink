package io.machinecode.chainlink.core.execution.artifact.writer;

import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteCheckpointException;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteCloseException;

import javax.batch.api.chunk.ItemWriter;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailCheckpointAndCloseEventOrderWriter implements ItemWriter {

    private int count = 0;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_CLOSE);
        throw new FailWriteCloseException();
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITE);
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_CHECKPOINT);
        if (count == 1) {
            throw new FailWriteCheckpointException();
        } else {
            ++count;
            return null;
        }
    }
}
