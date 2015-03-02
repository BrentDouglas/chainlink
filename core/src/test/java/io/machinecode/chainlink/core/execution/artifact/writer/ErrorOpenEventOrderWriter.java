package io.machinecode.chainlink.core.execution.artifact.writer;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailWriteOpenError;
import junit.framework.Assert;

import javax.batch.api.chunk.ItemWriter;
import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ErrorOpenEventOrderWriter implements ItemWriter {

    @Override
    public void open(final Serializable checkpoint) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_OPEN);
        throw new FailWriteOpenError();
    }

    @Override
    public void close() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.WRITER_CLOSE);
    }

    @Override
    public void writeItems(final List<Object> items) throws Exception {
        Assert.fail();
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        Assert.fail();
        return null;
    }
}
