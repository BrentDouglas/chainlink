package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.task.Task;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.factory.PropertyContext;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TaskWork extends Task, Work, Deferred<ExecutionContext, Throwable>, Serializable {

    TaskWork partition(PropertyContext context);

    boolean isPartitioned();

    void run(final Executor executor, final ExecutionContext context, final int timeout) throws Exception;
}
