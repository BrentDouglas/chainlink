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
public class AfterExecution extends ExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(AfterExecution.class);

    public AfterExecution(final ExecutionWork work, final Context context) {
        super(context.getJobExecutionId(), work);
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        try {
            final Plan plan = work.after(transport, context);

            if (plan == null) {
                return new DeferredImpl<Void>();
            }
            return transport.execute(jobExecutionId, this, plan);
        } catch (final Throwable e) {
            log.errorf(e, Message.format("work.execution.after.exception", jobExecutionId));
            context.setThrowable(e);
            return new DeferredImpl<Void>();
        }
    }
}
