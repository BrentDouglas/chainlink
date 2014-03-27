package io.machinecode.chainlink.spi.transport;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.util.Pair;

import javax.batch.operations.JobExecutionNotRunningException;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Transport extends Lifecycle {

    WorkerId generateWorkerId(final Worker worker);

    ExecutableId generateExecutableId(final Executable executable);

    DeferredId generateDeferredId(final Deferred<?> deferred);

    ExecutionRepositoryId generateExecutionRepositoryId(final ExecutionRepository repository);

    void registerExecutionRepository(final ExecutionRepositoryId id, final ExecutionRepository repository);

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id);

    ExecutionRepository unregisterExecutionRepository(final ExecutionRepositoryId id);

    //If already registered this should just return the original id
    void registerWorker(final WorkerId id , final Worker worker);

    Worker getWorker(final WorkerId id);

    Worker getWorker();

    List<Worker> getWorkers(final int required);

    Worker unregisterWorker(final WorkerId id);

    void registerDeferred(final long jobExecutionId, final DeferredId id, final Deferred<?> deferred);

    Deferred<?> getDeferred(final long jobExecutionId, final DeferredId id);

    Deferred<?> unregisterDeferred(final long jobExecutionId, final DeferredId id);

    void registerExecutable(final long jobExecutionId, final ExecutableId id, final Executable executable);

    Executable getExecutable(final long jobExecutionId, final ExecutableId id);

    Executable unregisterExecutable(final long jobExecutionId, final ExecutableId id);

    void registerJob(final long jobExecutionId, final Deferred<?> deferred);

    Deferred<?> getJob(long jobExecutionId) throws JobExecutionNotRunningException;

    void unregisterJob(long jobExecutionId) throws JobExecutionNotRunningException;
}
