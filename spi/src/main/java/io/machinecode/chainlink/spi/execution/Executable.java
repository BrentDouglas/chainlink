package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * <p>A piece of work to be done.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Executable {

    /**
     * @return The id of this {@link Executable}.
     */
    ExecutableId getId();

    /**
     * @return The id of the {@link Worker} that this executable needs to run
     *         or {@code null} if it can be run by any worker.
     */
    WorkerId getWorkerId();

    /**
     * @return
     */
    ExecutionContext getContext();

    void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext previous);
}