package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;

import javax.batch.runtime.context.JobContext;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class AfterJob extends ExecutableImpl<JobWork> {
    final long jobExecutionId;

    public AfterJob(final JobWork work, final Context context) {
        super(work);
        this.jobExecutionId = context.getJobExecutionId();
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        final JobContext jobContext = context.getJobContext();
        try {
            work.after(transport, context);
            return new DeferredImpl<Void>();
        } finally {
            Status.postJob(transport.getRepository(), jobExecutionId, jobContext.getBatchStatus(), jobContext.getExitStatus());
            transport.finishJob(jobExecutionId);
        }
    }
}
