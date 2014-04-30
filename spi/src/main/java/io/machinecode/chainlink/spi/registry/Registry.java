package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.On;

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

    ChainId registerJob(final long jobExecutionId, final ChainId chainId, final Chain<?> chain) throws JobExecutionIsRunningException;

    Chain<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    JobRegistry getJobRegistry(long jobExecutionId);

    void unregisterJob(long jobExecutionId);

    void onRegisterJob(final On<Long> on);

    /**
     * Should only be executed if the main job Deferred is registered in this repository
     * @param on
     */
    void onUnregisterJob(final On<Long> on);
}
