package io.machinecode.chainlink.core.execution.artifact.processor;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailProcessException;

import javax.batch.api.chunk.ItemProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailTripleTwiceEventOrderProcessor implements ItemProcessor {

    int count = 0;

    @Override
    public Object processItem(final Object item) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.PROCESS);
        try {
            switch (count) {
                case 1:
                    throw new Exception();
                case 2:
                case 3:
                    throw new FailProcessException();
                case 7:
                    throw new Exception();
                case 8:
                case 9:
                    throw new FailProcessException();
                default:
                    return item;
            }
        } finally {
            ++count;
        }
    }
}
