package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Registry extends Lifecycle {

    void registerRepository(final RepositoryId repositoryId, final Repository repository);

    Repository getRepository(final RepositoryId id);

    Repository unregisterRepository(final RepositoryId id);

    Promise<?,?,?> registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain);

    Promise<?,?,?> unregisterJob(long jobExecutionId);

    Chain<?> getJob(long jobExecutionId);

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
