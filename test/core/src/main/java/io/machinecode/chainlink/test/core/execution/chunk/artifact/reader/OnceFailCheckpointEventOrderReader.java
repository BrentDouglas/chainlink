package io.machinecode.chainlink.test.core.execution.chunk.artifact.reader;

import io.machinecode.chainlink.test.core.execution.chunk.artifact.ChunkEvent;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.test.core.execution.chunk.artifact.exception.FailReadCheckpointException;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class OnceFailCheckpointEventOrderReader implements ItemReader {

    private int count = 0;
    private boolean thrown = false;

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READER_OPEN);
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READER_CLOSE);
    }

    @Override
    public Object readItem() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READ);
        if (count == 2) {
            return null;
        } else {
            ++count;
            return new Object();
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        EventOrderAccumulator._order.add(ChunkEvent.READER_CHECKPOINT);
        if (!thrown) {
            thrown = true;
            throw new FailReadCheckpointException();
        }
        return null;
    }
}
