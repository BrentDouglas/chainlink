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
public abstract class ExecutableImpl<T extends Work> extends DeferredImpl<Deferred<?>> implements Executable {

    private static final Logger log = Logger.getLogger(ExecutableImpl.class);

    protected static final int RUNNING  = 4;

    protected final T work;

    protected final CallbackExecutable parent;
    protected final ExecutionContext context;

    public ExecutableImpl(final CallbackExecutable parent, final ExecutionContext context, final T work) {
        super(new Deferred[1]);
        this.parent = parent;
        this.context = context;
        this.work = work;
    }

    @Override
    public CallbackExecutable getParent() {
        return parent;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
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
            final Deferred<?> next = doExecute(executor, threadId, parentExecutable, contexts);
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

    protected abstract Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                               final ExecutionContext... contexts) throws Throwable;
}
