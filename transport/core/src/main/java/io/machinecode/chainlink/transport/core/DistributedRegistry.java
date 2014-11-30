package io.machinecode.chainlink.transport.core;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.core.cmd.DistributedCommand;
import io.machinecode.then.api.Deferred;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface DistributedRegistry<A, R extends DistributedRegistry<A, R>> extends Registry {

    <T> void invoke(final A address, final DistributedCommand<T, A, R> command, final Deferred<T,Throwable,?> promise);

    <T> void invoke(final A address, final DistributedCommand<T, A, R> command, final Deferred<T,Throwable,?> promise, final long timeout, final TimeUnit unit);

    A getLocal();

    ExecutionRepository getLocalExecutionRepository(final ExecutionRepositoryId id);

    boolean hasWorker(final WorkerId workerId);

    DistributedWorkerId<A> leastBusyWorker();
}
