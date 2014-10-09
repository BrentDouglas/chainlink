package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;

import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobExecutionNotRunningException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Registry extends AutoCloseable {

    ExecutionRepositoryId registerExecutionRepository(final ExecutionRepositoryId repositoryId, final ExecutionRepository repository);

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id);

    ExecutionRepository unregisterExecutionRepository(final ExecutionRepositoryId id);

    Promise<?,?,?> registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) throws JobExecutionIsRunningException;

    Promise<?,?,?> unregisterJob(long jobExecutionId);

    Chain<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    void registerChain(final long jobExecutionId, final ChainId id, final Chain<?> chain);

    Chain<?> getChain(final long jobExecutionId, final ChainId id);

    void registerExecutable(final long jobExecutionId, final Executable executable);

    Executable getExecutable(final long jobExecutionId, final ExecutableId id);

    StepAccumulator getStepAccumulator(final long jobExecutionId, final String id);

    SplitAccumulator getSplitAccumulator(final long jobExecutionId, final String id);

    <T> T loadArtifact(final Class<T> clazz, final String ref, final ExecutionContext context);

    <T> void storeArtifact(final Class<T> clazz, final String ref, final ExecutionContext context, final T value);

    void registerJobEventListener(final String key, final JobEventListener on);

    void unregisterJobEventListener(final String key);
}
