package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TaskWork extends Task, Work, Serializable {

    TaskWork partition(PropertyContext context);

    void cancel(final RuntimeConfiguration configuration, final ExecutionContext context);

    void run(final RuntimeConfiguration configuration, final Deferred<?> deferred, final ExecutionRepositoryId executionRepositoryId,
             final ExecutionContext context, final int timeout) throws Throwable;
}
