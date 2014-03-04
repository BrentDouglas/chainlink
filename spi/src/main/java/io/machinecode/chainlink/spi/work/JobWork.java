package io.machinecode.chainlink.spi.work;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.context.ThreadId;
import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.element.Element;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobWork extends Job, Work, Serializable {

    Deferred<?> before(final Executor executor, final ThreadId threadId, final Executable callback,
                       final ExecutionContext context) throws Exception;

    void after(final Executor executor, final ThreadId threadId,
               final Executable callback, final ExecutionContext context) throws Exception;

    /**
     * @param next The id of the element to extract
     * @return The element in the job with id {@param next}
     */
    ExecutionWork getNextExecution(final String next);

    boolean isRestartable();
}
