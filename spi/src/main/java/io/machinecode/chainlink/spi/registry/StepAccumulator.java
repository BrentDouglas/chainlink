package io.machinecode.chainlink.spi.registry;

import javax.batch.api.partition.PartitionReducer;
import javax.transaction.Transaction;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public interface StepAccumulator extends Accumulator {;

    PartitionReducer.PartitionStatus getPartitionStatus();

    void setPartitionStatus(final PartitionReducer.PartitionStatus partitionStatus);

    Transaction getTransaction();

    void setTransaction(final Transaction transaction);
}
