package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunJob extends ExecutableImpl<JobWork> {
    final long jobExecutionId;

    public RunJob(final JobWork work, final Context context) {
        super(work);
        this.jobExecutionId = context.getJobExecutionId();
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        work.before(transport, context);
        final Plan plan = work.run(transport, context);
        if (plan == null) {
            return new DeferredImpl<Void>();
        }
        return transport.execute(jobExecutionId, this, plan);
    }
}
