package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.ExecutionWork;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunExecution extends ExecutableImpl<ExecutionWork> {
    final long jobExecutionId;

    public RunExecution(final ExecutionWork work, final Context context) {
        super(work);
        this.jobExecutionId = context.getJobExecutionId();
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);

        final Plan before = work.before(transport, context);
        final Plan run = work.run(transport, context);
        return new DeferredImpl<Void>(
                before == null ? new DeferredImpl<Void>() : transport.execute(jobExecutionId, this, before),
                run == null ? new DeferredImpl<Void>() : transport.execute(jobExecutionId, this, run)
        );
    }
}
