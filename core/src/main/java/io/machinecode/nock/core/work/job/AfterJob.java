package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.JobWork;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class AfterJob extends ExecutableImpl {
    final JobWork work;
    final Context context;

    public AfterJob(final JobWork work, final Context context) {
        this.work = work;
        this.context = context;
    }

    @Override
    public void run(final Transport transport) throws Exception {
        work.after(transport, context);
        Status.postJob(transport, context);
    }
}
