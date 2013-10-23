package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

import javax.batch.runtime.context.JobContext;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class AfterJob extends ExecutableImpl<JobWork> {

    private static final Logger log = Logger.getLogger(AfterJob.class);

    public AfterJob(final JobWork work, final Context context) {
        super(context.getJobExecutionId(), work);
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        final JobContext jobContext = context.getJobContext();
        try {
            work.after(transport, context);
        } catch (final Throwable e) {
            log.error("", e); //TODO Message
            context.setThrowable(e);
        } finally {
            if (context.getThrowable() == null) {
                Status.completedJob(transport.getRepository(), jobExecutionId, jobContext == null ? null : jobContext.getExitStatus());
            } else {
                Status.failedJob(transport.getRepository(), jobExecutionId, jobContext == null ? null : jobContext.getExitStatus());
            }
            transport.finalizeJob(jobExecutionId);
        }
        return new DeferredImpl<Void>();
    }
}
