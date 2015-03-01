package io.machinecode.chainlink.core.execution.chunk.artifact;

import java.util.LinkedList;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class EventOrderAccumulator {

    public static final LinkedList<OrderEvent> _order = new LinkedList<OrderEvent>();

    public static OrderEvent[] order() {
        return _order.toArray(new OrderEvent[_order.size()]);
    }

    public static void reset() {
        _order.clear();
    }
}
