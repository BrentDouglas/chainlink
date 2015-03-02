package io.machinecode.chainlink.core.execution.artifact.writer;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteException;

import javax.batch.api.chunk.ItemWriter;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailTripleTwiceEventOrderWriter implements ItemWriter {

    private int count = 0;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_CLOSE);
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITE);
        try {
            switch (count) {
                case 1:
                    throw new Exception();
                case 2:
                case 3:
                    throw new FailWriteException();
                case 7:
                    throw new Exception();
                case 8:
                case 9:
                    throw new FailWriteException();
            }
        } finally {
            ++count;
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_CHECKPOINT);
        return null;
    }
}
