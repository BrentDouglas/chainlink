package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;
import org.jboss.logging.Logger;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunJob extends ExecutableImpl<JobWork> {

    private static final Logger log = Logger.getLogger(RunJob.class);

    public RunJob(final JobWork work, final Context context) {
        super(context.getJobExecutionId(), work);
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        final Context context = transport.getContext(jobExecutionId);
        try {
            work.before(transport, context);
            final Plan plan = work.run(transport, context);
            if (plan == null) {
                return new DeferredImpl<Void>();
            }
            return transport.execute(jobExecutionId, this, plan);
        } catch (final Throwable e) {
            log.error("", e); //TODO Message
            context.setThrowable(e);
            return new DeferredImpl<Void>();
        }
    }
}
