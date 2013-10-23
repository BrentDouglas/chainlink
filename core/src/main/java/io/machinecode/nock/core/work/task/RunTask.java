package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.TaskWork;
import org.jboss.logging.Logger;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunTask extends ExecutableImpl<TaskWork> {

    private static final Logger log = Logger.getLogger(RunTask.class);

    final int timeout;

    public RunTask(final TaskWork work, final Context context, final int timeout) {
        super(context.getJobExecutionId(), work);
        this.timeout = timeout;
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        try {
            work.run(transport, context, timeout);
        } catch (final Throwable e) {
            log.error("", e); //TODO Message
            context.setThrowable(e);
        }
        return work;
    }

    @Override
    public boolean cancel(final boolean mayInterruptIfRunning) {
        return work.cancel(mayInterruptIfRunning)
                && super.cancel(mayInterruptIfRunning);
    }
}
