package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.registry.WorkerId;
import io.machinecode.then.api.Promise;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Worker extends Runnable, AutoCloseable {

    void start();

    WorkerId id();

    void execute(final ExecutableEvent event);

    void callback(final CallbackEvent event);

    Promise<ChainAndIds,Throwable,?> chain(final long jobExecutionId);
}
