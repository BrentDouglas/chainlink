package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunExecution extends ExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(RunExecution.class);

    public RunExecution(final ExecutionWork work, final Context context) {
        super(context.getJobExecutionId(), work);
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        try {
            final Plan before = work.before(transport, context);
            final Plan run = work.run(transport, context);
            return new DeferredImpl<Void>(
                    before == null ? new DeferredImpl<Void>() : transport.execute(jobExecutionId, this, before),
                    run == null ? new DeferredImpl<Void>() : transport.execute(jobExecutionId, this, run)
            );
        } catch (final Throwable e) {
            log.errorf(e, Message.format("work.execution.run.exception", jobExecutionId));
            context.setThrowable(e);
            return new DeferredImpl<Void>();
        }
    }
}
