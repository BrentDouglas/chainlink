package io.machinecode.nock.core.work.job;

import io.machinecode.nock.core.work.FailureImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;

/**
* Brent Douglas <brent.n.douglas@gmail.com>
*/
public class FailJob extends FailureImpl {
    final Context context;

    public FailJob(final Context context) {
        this.context = context;
    }

    @Override
    public void doFail(final Transport transport, final Exception exception) {
        Status.failed(transport, context);
    }
}
