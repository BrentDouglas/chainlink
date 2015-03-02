package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;

import javax.batch.api.partition.PartitionReducer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestReducer implements PartitionReducer {

    @Override
    public void beginPartitionedStep() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.REDUCE_BEGIN_STEP);
    }

    @Override
    public void beforePartitionedStepCompletion() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.REDUCE_BEFORE_COMPLETION);
    }

    @Override
    public void rollbackPartitionedStep() throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.REDUCE_ROLLBACK);
    }

    @Override
    public void afterPartitionedStepCompletion(final PartitionStatus status) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.REDUCE_AFTER_COMPLETION);
    }
}
