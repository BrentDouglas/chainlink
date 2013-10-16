package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.ExecutableImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class FailJob extends ExecutableImpl {
    final Context context;

    public FailJob(final Context context) {
        this.context = context;
    }

    @Override
    public Deferred run(final Transport transport) throws Exception {
        Status.failed(transport, context);
        return new DeferredImpl();
    }
}
