package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.then.Chain;

import javax.batch.api.partition.PartitionReducer;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import static javax.batch.api.partition.PartitionReducer.PartitionStatus;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobRegistry {

    ChainId registerChain(final ChainId id, final Chain<?> chain);

    Chain<?> getChain(final ChainId id);

    ExecutableId registerExecutable(final ExecutableId executableId, final Executable executable);

    Executable getExecutable(final ExecutableId id);

    StepAccumulator getStepAccumulator(final String id);

    Accumulator getSplitAccumulator(final String id);

    class Accumulator {

        private long count = 0;

        public long incrementAndGetCallbackCount() {
            return ++count;
        }
    }

    class StepAccumulator extends Accumulator {

        private PartitionStatus partitionStatus;
        private Transaction transaction;

        public PartitionStatus getPartitionStatus() {
            return partitionStatus;
        }

        public void setPartitionStatus(final PartitionStatus partitionStatus) {
            this.partitionStatus = partitionStatus;
        }

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(final Transaction transaction) {
            this.transaction = transaction;
        }
    }
}
