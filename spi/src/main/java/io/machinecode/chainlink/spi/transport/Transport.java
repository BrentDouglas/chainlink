package io.machinecode.chainlink.spi.transport;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.then.api.Deferred;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Transport<A> extends AutoCloseable {

    <T> Future<T> invokeLocal(final Command<T, A> command) throws Exception;

    <T> void invokeRemote(final A address, final Command<T, A> command, final Deferred<T, Throwable,?> promise);

    <T> void invokeRemote(final A address, final Command<T, A> command, final Deferred<T, Throwable,?> promise, final long timeout, final TimeUnit unit);

    A getLocal();

    Registry getRegistry();

    void registerWorker(final WorkerId id, final Worker worker);

    Worker getWorker(final WorkerId id);

    Worker getWorker();

    List<Worker> getWorkers(final int required);

    Worker getWorker(final long jobExecutionId, final ExecutableId executableId);

    Worker unregisterWorker(final WorkerId id);

    boolean hasWorker(final WorkerId workerId);

    boolean hasWorker(final long jobExecutionId, final ExecutableId executableId);

    WorkerId leastBusyWorker();

    ExecutionRepository getExecutionRepository(final ExecutionRepositoryId id);

    Executable getExecutable(final long jobExecutionId, final ExecutableId id);

    ChainId generateChainId();

    ExecutableId generateExecutableId();

    WorkerId generateWorkerId(final Worker worker);

    ExecutionRepositoryId generateExecutionRepositoryId();

    long getTimeout();

    TimeUnit getTimeUnit();
}
