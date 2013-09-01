package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobWork extends Job, Serializable {

    void before(final Transport transport, final Context context) throws Exception;

    Future<Void> runJob(final Worker worker, final Transport transport, final Context context) throws Exception;

    void after(final Transport transport, final Context context) throws Exception;

    ExecutionWork next(final String next);
}