package io.machinecode.chainlink.core.execution.chunk.artifact.reader;

import io.machinecode.chainlink.core.execution.chunk.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.chunk.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailReadCloseException;
import io.machinecode.chainlink.core.execution.chunk.artifact.exception.FailReadOpenException;
import junit.framework.Assert;

import javax.batch.api.chunk.ItemReader;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailOpenAndCloseEventOrderReader implements ItemReader {

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_OPEN);
        throw new FailReadOpenException();
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.READER_CLOSE);
        throw new FailReadCloseException();
    }

    @Override
    public Object readItem() throws Exception {
        Assert.fail();
        return null;
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        Assert.fail();
        return null;
    }
}
