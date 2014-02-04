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
public abstract class CallbackExecutableImpl<T extends Work> extends DeferredImpl<Deferred<?>> implements CallbackExecutable {

    private static final Logger log = Logger.getLogger(CallbackExecutableImpl.class);

    protected static final int RUNNING_EXECUTE  = 4;
    protected static final int EXECUTED         = 5;
    protected static final int RUNNING_CALLBACK = 6;

    protected final T work;

    protected final ExecutionContext context;

    public CallbackExecutableImpl(final ExecutionContext context, final T work) {
        super(new Deferred[2]);
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
                state = RUNNING_EXECUTE;
            }
            log.tracef(Messages.format("executable.execute", context.getJobExecutionId(), this.getClass().getSimpleName()));
            chain[0] = doExecute(executor, threadId, parentExecutable, contexts);
            state = EXECUTED;
        } catch (final Throwable e) {
            log.infof(e, Messages.format("executable.execute.exception", context.getJobExecutionId(), this.getClass().getSimpleName()));
            reject(e);
        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @Override
    public void callback(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                         final ExecutionContext childContext) {
        try {
            synchronized (this) {
                if (isCancelled()) {
                    return;
                }
                if (state != EXECUTED) {
                    reject(new IllegalStateException()); //TODO
                    return;
                }
                state = RUNNING_CALLBACK;
            }
            log.tracef(Messages.format("executable.callback", context.getJobExecutionId(), this.getClass().getSimpleName()));
            resolve(chain[1] = doCallback(executor, threadId, parentExecutable, childContext));
        } catch (final Throwable e) {
            log.infof(e, Messages.format("executable.callback.exception", context.getJobExecutionId(), this.getClass().getSimpleName()));
            reject(e);
        } finally {
            synchronized (this) {
                notifyAll();
            }
        }
    }

    protected abstract Deferred<?> doExecute(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                               final ExecutionContext... contexts) throws Throwable;

    protected abstract Deferred<?> doCallback(final Executor executor, final ThreadId threadId, final CallbackExecutable parentExecutable,
                                                final ExecutionContext childContext) throws Throwable;
}
