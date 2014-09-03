package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;

import javax.batch.api.partition.PartitionReducer;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotRunningException;
import javax.transaction.Transaction;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Registry extends Lifecycle {

    ChainId generateChainId();

    ExecutableId generateExecutableId();

    WorkerId generateWorkerId(final Worker worker);

    ExecutionRepositoryId generateExecutionRepositoryId();

    ExecutionRepositoryId registerExecutionRepository(final ExecutionRepositoryId repositoryId, final ExecutionRepository repository);

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id);

    ExecutionRepository unregisterExecutionRepository(final ExecutionRepositoryId id);

    void registerWorker(final WorkerId id, final Worker worker);

    Worker getWorker(final WorkerId id);

    Worker getWorker();

    List<Worker> getWorkers(final int required);

    Worker unregisterWorker(final WorkerId id);

    ChainId registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) throws JobExecutionIsRunningException;

    Chain<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    void unregisterJob(long jobExecutionId);

    ChainId registerChain(final long jobExecutionId, final ChainId id, final Chain<?> chain);

    Chain<?> getChain(final long jobExecutionId, final ChainId id);

    void registerExecutable(final long jobExecutionId, final Executable executable);

    Executable getExecutable(final long jobExecutionId, final ExecutableId id);

    StepAccumulator getStepAccumulator(final long jobExecutionId, final String id);

    SplitAccumulator getSplitAccumulator(final long jobExecutionId, final String id);

    interface Accumulator {

        long incrementAndGetCallbackCount();
    }

    interface SplitAccumulator extends Accumulator {

        long[] getPriorStepExecutionIds();

        void addPriorStepExecutionId(final long priorStepExecutionId);
    }

    interface StepAccumulator extends Accumulator {;

        PartitionReducer.PartitionStatus getPartitionStatus();

        void setPartitionStatus(final PartitionReducer.PartitionStatus partitionStatus);

        Transaction getTransaction();

        void setTransaction(final Transaction transaction);
    }
}
