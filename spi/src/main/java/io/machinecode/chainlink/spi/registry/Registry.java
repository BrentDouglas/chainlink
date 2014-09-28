package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.management.JobOperation;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;

import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotRunningException;
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

    void registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) throws JobExecutionIsRunningException;

    Chain<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    void unregisterJob(long jobExecutionId);

    void registerChain(final long jobExecutionId, final ChainId id, final Chain<?> chain);

    Chain<?> getChain(final long jobExecutionId, final ChainId id);

    void registerExecutableAndContext(final long jobExecutionId, final Executable executable, final ExecutionContext context);

    ExecutableAndContext getExecutableAndContext(final long jobExecutionId, final ExecutableId id);

    StepAccumulator getStepAccumulator(final long jobExecutionId, final String id);

    SplitAccumulator getSplitAccumulator(final long jobExecutionId, final String id);

    <T> T loadArtifact(final Class<T> clazz, final String ref, final ExecutionContext context);

    <T> void storeArtifact(final Class<T> clazz, final String ref, final ExecutionContext context, final T value);
}
