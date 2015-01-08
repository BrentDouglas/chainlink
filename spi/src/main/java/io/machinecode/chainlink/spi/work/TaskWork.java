package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.then.api.Promise;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface TaskWork extends Task, Work, Serializable {

    TaskWork partition(PropertyContext context);

    void cancel(final Configuration configuration, final ExecutionContext context);

    void run(final Configuration configuration, final Promise<?,Throwable,?> promise, final ExecutionRepositoryId executionRepositoryId,
             final ExecutionContext context, final int timeout) throws Throwable;
}
