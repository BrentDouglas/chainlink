package io.machinecode.chainlink.test.core.execution.chunk.artifact;

import java.util.LinkedList;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class EventOrderAccumulator {

    public static LinkedList<ChunkEvent> _order = new LinkedList<ChunkEvent>();

    public static ChunkEvent[] order() {
        return _order.toArray(new ChunkEvent[_order.size()]);
    }

    public static void reset() {
        _order.clear();
    }
}
