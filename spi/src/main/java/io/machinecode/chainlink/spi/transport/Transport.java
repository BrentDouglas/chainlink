package io.machinecode.chainlink.spi.transport;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.then.api.Deferred;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Transport<A> extends Lifecycle {

    <T> void invokeRemote(final A address, final Command<T, A> command, final Deferred<T, Throwable,?> promise,
                          final long timeout, final TimeUnit unit);

    A getAddress();

    Worker getWorker(final WorkerId id) throws Exception;

    Worker getWorker() throws Exception;

    List<Worker> getWorkers(final int required) throws Exception;

    Worker getWorker(final long jobExecutionId, final ExecutableId executableId) throws Exception;

    boolean hasWorker(final WorkerId workerId);

    boolean hasWorker(final long jobExecutionId, final ExecutableId executableId);

    void registerWorker(final Worker worker);

    void unregisterWorker(final Worker worker);

    WorkerId leastBusyWorker() throws Exception;

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id) throws Exception;

    Executable getExecutable(final long jobExecutionId, final ExecutableId id) throws Exception;

    ChainId generateChainId();

    ExecutableId generateExecutableId();

    WorkerId generateWorkerId(final Worker worker);

    ExecutionRepositoryId generateExecutionRepositoryId();

    long getTimeout();

    TimeUnit getTimeUnit();
}
