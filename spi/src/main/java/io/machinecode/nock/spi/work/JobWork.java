package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Pair;

import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobWork extends Job, Work, Serializable {

    Deferred<?,?> before(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
                         final CallbackExecutable parentExecutable, final ExecutionContext context) throws Exception;

    void after(final Executor executor, final ThreadId threadId, final CallbackExecutable thisExecutable,
               final CallbackExecutable parentExecutable, final ExecutionContext context,
               final ExecutionContext childContext) throws Exception;

    /**
     * @param next The id of the element to extract
     * @return The element in the job with id {@param next}
     */
    ExecutionWork getNextExecution(final String next);

    /**
     * @param element The element to get the properties for
     * @return A list of all properties
     */
    List<? extends Pair<String, String>> getProperties(final Element element);
}
