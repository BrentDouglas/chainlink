package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.JobWork;
import io.machinecode.nock.spi.work.Worker;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class RunJob extends ExecutableImpl {
    final Worker worker;
    final JobWork work;
    final Context context;

    public RunJob(final Worker worker, final JobWork work, final Context context) {
        this.worker = worker;
        this.work = work;
        this.context = context;
    }

    @Override
    public void run(final Transport transport) throws Exception {
        work.before(transport, context);
        work.runJob(worker, transport, context);
    }
}
