package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Planned {

    Plan plan(final Transport transport, final Context context);
}
