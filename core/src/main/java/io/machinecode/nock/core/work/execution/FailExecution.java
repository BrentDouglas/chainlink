package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class FailExecution extends ExecutableImpl<ExecutionWork> {

    private static final Logger log = Logger.getLogger(FailExecution.class);

    public FailExecution(final ExecutionWork work, final Context context) {
        super(context.getJobExecutionId(), work);
    }

    @Override
    public Deferred<?> run(final Transport transport) throws Exception {
        Status.failedJob(transport.getRepository(), jobExecutionId, transport.getContext(jobExecutionId).getJobContext().getExitStatus());
        return new DeferredImpl<Void>();
    }
}
