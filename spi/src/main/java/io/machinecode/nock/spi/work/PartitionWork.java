package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.transport.Executable;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionWork<T extends Strategy> extends Partition<T>, Serializable {

    Executable[] map(final TaskWork task, final Worker worker, final Transport transport, final Context context) throws Exception;

    void collect(final TaskWork task, final Worker worker, final Transport transport, final Context context) throws Exception;

    void analyse(final TaskWork task, final Worker worker, final Transport transport, final Context context) throws Exception;
}
