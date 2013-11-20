package io.machinecode.nock.core.work;

import io.machinecode.nock.core.deferred.DeferredImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.context.ThreadId;
import io.machinecode.nock.spi.deferred.Deferred;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.Work;
import org.jboss.logging.Logger;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class ExecutableImpl<T extends Work> extends DeferredImpl<Deferred<?,?>, Throwable> implements Executable {

    private static final Logger log = Logger.getLogger(ExecutableImpl.class);

    protected static final int RUNNING  = 4;

    protected final T work;

    protected final ExecutionContext context;

    public ExecutableImpl(final ExecutionContext context, final T work) {
        super(new Deferred[1]);
        this.context = context;
        this.work = work;
    }

    @Override
    public void execute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                        final ExecutionContext... contexts) {
        try {
            synchronized (this) {
                if (isCancelled()) {
                    return;
                }
                if (state != PENDING) {
                    reject(new IllegalStateException()); //TODO
                    return;
                }
                state = RUNNING;
            }
            log.tracef(Messages.format("executable.execute", context.getJobExecutionId(), this.getClass().getSimpleName()));
            final Deferred<?,?> next = doExecute(executor, threadId, parentExecutable, contexts);
            chain[0] = next;
            resolve(next);
        } catch (final Throwable e) {
            log.infof(e, Messages.format("executable.execute.exception", context.getJobExecutionId(), this.getClass().getSimpleName()));
            reject(e);
        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    protected abstract Deferred<?,?> doExecute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                               final ExecutionContext... contexts) throws Throwable;
}
