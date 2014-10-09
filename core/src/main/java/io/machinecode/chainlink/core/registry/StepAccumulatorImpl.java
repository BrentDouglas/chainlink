package io.machinecode.chainlink.core.registry;

import io.machinecode.chainlink.spi.registry.StepAccumulator;

import javax.batch.api.partition.PartitionReducer;
import javax.transaction.Transaction;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class StepAccumulatorImpl extends AccumulatorImpl implements StepAccumulator {

    private PartitionReducer.PartitionStatus partitionStatus;
    private Transaction transaction;

    @Override
    public PartitionReducer.PartitionStatus getPartitionStatus() {
        return partitionStatus;
    }

    @Override
    public void setPartitionStatus(final PartitionReducer.PartitionStatus partitionStatus) {
        this.partitionStatus = partitionStatus;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public void setTransaction(final Transaction transaction) {
        this.transaction = transaction;
    }
}
