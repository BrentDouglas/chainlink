package io.machinecode.chainlink.spi.registry;

import javax.batch.api.partition.PartitionReducer;
import javax.transaction.Transaction;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public interface StepAccumulator {;

    long incrementAndGetCallbackCount();

    PartitionReducer.PartitionStatus getPartitionStatus();

    void setPartitionStatus(final PartitionReducer.PartitionStatus partitionStatus);

    Transaction getTransaction();

    void setTransaction(final Transaction transaction);
}
