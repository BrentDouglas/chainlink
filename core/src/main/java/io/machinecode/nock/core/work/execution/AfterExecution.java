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
public class AfterExecution extends ExecutableImpl<ExecutionWork> {
    final long jobExecutionId;

    public AfterExecution(final ExecutionWork work, final Context context) {
        super(work);
        this.jobExecutionId = context.getJobExecutionId();
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Plan plan = work.after(transport, transport.getContext(jobExecutionId));
        if (plan == null) {
            return new DeferredImpl<Void>();
        }
        return transport.execute(jobExecutionId, this, plan);
    }
}
