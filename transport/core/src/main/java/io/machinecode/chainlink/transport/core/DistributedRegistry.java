package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.api.Promise;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface DistributedRegistry<A, R extends DistributedRegistry<A, R>> extends Registry {

    <T> void invoke(final A address, final DistributedCommand<T, A, R> command, final Promise<T,Throwable> promise);

    <T> void invoke(final A address, final DistributedCommand<T, A, R> command, final Promise<T,Throwable> promise, final long timeout, final TimeUnit unit);

    A getLocal();

    ExecutionRepository getLocalExecutionRepository(final ExecutionRepositoryId id);

    boolean hasWorker(final WorkerId workerId);

    DistributedWorkerId<A> leastBusyWorker();
}
