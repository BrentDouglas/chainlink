package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;
import io.machinecode.chainlink.core.execution.artifact.exception.FailCollectorException;

import javax.batch.api.partition.PartitionCollector;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FailTestCollector implements PartitionCollector {

    @Override
    public Serializable collectPartitionData() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.COLLECT);
        throw new FailCollectorException();
    }
}
