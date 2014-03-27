package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.Lifecycle;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.transport.DeferredId;
import io.machinecode.chainlink.spi.transport.WorkerId;
import io.machinecode.chainlink.spi.util.Pair;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Worker extends Runnable, Lifecycle {

    WorkerId getWorkerId();

    void addExecutable(final ExecutableEvent event);

    Pair<DeferredId, Deferred<?>> createDistributedDeferred(final Executable executable);
}
