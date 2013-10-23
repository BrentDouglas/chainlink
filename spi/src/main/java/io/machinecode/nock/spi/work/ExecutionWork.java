package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionWork extends Execution, Work, Planned, Serializable {

    Plan before(final Transport transport, final Context context) throws Exception;

    Plan run(final Transport transport, final Context context) throws Exception;

    Plan after(final Transport transport, final Context context) throws Exception;
}
