package io.machinecode.chainlink.spi.transport;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Transport extends Lifecycle, Addressed {

    /**
     * <p>This method MUST ensure that each of the {@link Executable}'s are submitted to
     * {@link io.machinecode.chainlink.spi.execution.Worker#execute(io.machinecode.chainlink.spi.execution.ExecutableEvent)}
     * exactly once to a single {@link io.machinecode.chainlink.spi.execution.Worker}.</p>
     *
     * @param maxThreads The maximum number of threads that the {@link Executable}'s can be run in.
     * @param executables The {@link Executable}'s that are to be executed.
     * @return A promise of a chain that will be resolved after execution has completed for the provided executables
     *         and any children they spawn.
     * @throws Exception On any implementation specific errors.
     */
    Promise<Chain<?>,Throwable,Object> distribute(final int maxThreads, final Executable... executables) throws Exception;

    /**
     * <p>This method MUST ensure that the {@link Executable} represented by executableId is submitted to
     * {@link io.machinecode.chainlink.spi.execution.Worker#callback(io.machinecode.chainlink.spi.execution.CallbackEvent)}
     * exactly once to a single {@link io.machinecode.chainlink.spi.execution.Worker}. The worker it is submitted to
     * MUST have the same {@link io.machinecode.chainlink.spi.execution.WorkerId} as the {@link Executable} represented
     * by executableId.</p>
     *
     * @param executableId The id of the callback to be run.
     * @param context The {@link ExecutionContext} of the child element. e.g. For a step it may be that of a
     *                {@link javax.batch.api.Batchlet}, etc.
     * @return A promise of a chain that will be resolved after execution has completed for the {@link Executable}
     *         identified by the provided {@link ExecutableId} and any children it spawns.
     * @throws Exception On any implementation specific errors.
     */
    Promise<Chain<?>,Throwable,Object> callback(final ExecutableId executableId, final ExecutionContext context) throws Exception;

    /**
     * @param id The id of the {@link Repository} to return.
     * @return The {@link Repository} identified by {@code id}.
     * @throws Exception On any implementation specific errors.
     */
    Repository getRepository(final RepositoryId id) throws Exception;

    /**
     * <p>Together with {@link #getTimeUnit()}, this value represents the maximum duration allowed when performing any
     * remote operations.</p>
     *
     * @return The magnitude of the duration.
     * @see #getTimeUnit()
     */
    long getTimeout();

    /**
     * @return The {@link TimeUnit} of the duration.
     * @see #getTimeout()
     */
    TimeUnit getTimeUnit();
}
