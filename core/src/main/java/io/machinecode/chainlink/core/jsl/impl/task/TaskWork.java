package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.spi.jsl.task.Task;
import io.machinecode.chainlink.spi.registry.RepositoryId;
import io.machinecode.then.api.Promise;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface TaskWork extends Task, Serializable {

    TaskWork partition(PropertyContext context);

    void cancel(final Configuration configuration, final ExecutionContext context);

    void run(final Configuration configuration, final Promise<?,Throwable,?> promise, final RepositoryId repositoryId,
             final ExecutionContext context, final int timeout) throws Throwable;
}
