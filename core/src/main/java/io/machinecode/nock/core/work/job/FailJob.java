package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class FailJob extends ExecutableImpl<JobWork> {
    final long jobExecutionId;

    public FailJob(final JobWork work, final Context context) {
        super(work);
        this.jobExecutionId = context.getJobExecutionId();
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        Status.failedJob(transport.getRepository(), jobExecutionId, transport.getContext(jobExecutionId).getJobContext().getExitStatus());
        return new DeferredImpl<Void>();
    }
}
