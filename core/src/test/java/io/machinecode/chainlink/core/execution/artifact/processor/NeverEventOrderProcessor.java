package io.machinecode.chainlink.core.execution.artifact.processor;

import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;

import javax.batch.api.chunk.ItemProcessor;

/**
 * Never filters.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NeverEventOrderProcessor implements ItemProcessor {

    @Override
    public Object processItem(final Object item) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.PROCESS);
        return item;
    }
}
