package io.machinecode.chainlink.core.execution.artifact.partition;

import io.machinecode.chainlink.core.execution.artifact.EventOrderAccumulator;
import io.machinecode.chainlink.core.execution.artifact.OrderEvent;

import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestAnalyzer implements PartitionAnalyzer {

    @Override
    public void analyzeCollectorData(final Serializable data) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.ANALYZE_DATA);
    }

    @Override
    public void analyzeStatus(final BatchStatus batchStatus, final String exitStatus) throws Exception {
        EventOrderAccumulator._order.add(OrderEvent.ANALYZE_STATUS);
    }
}
