package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.JobWork;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunJob extends ExecutableImpl {
    final JobWork work;
    final Context context;

    public RunJob(final JobWork work, final Context context) {
        this.work = work;
        this.context = context;
    }

    @Override
    public Deferred run(final Transport transport) throws Exception {
        work.before(transport, context);
        return work.run(transport, context);
    }
}
