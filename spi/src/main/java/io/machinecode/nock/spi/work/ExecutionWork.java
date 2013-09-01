package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionWork extends Execution, Serializable {

    Future<Void> before(final Worker worker, final Transport transport, final Context context) throws Exception;

    Future<Void> run(final Worker worker, final Transport transport, final Context context) throws Exception;

    Future<Void> after(final Worker worker, final Transport transport, final Context context) throws Exception;
}
