package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.task.Task;
import io.machinecode.nock.spi.factory.PropertyContext;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TaskWork extends Task, Serializable {

    TaskWork partition(PropertyContext context);

    void run(final Worker worker, final Transport transport, final Context context) throws Exception;

    void stop() throws Exception;
}
