package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionWork<T extends Strategy> extends Partition<T>, Serializable {

    PartitionTarget map(final TaskWork task, final Transport transport, final Context context, final int timeout) throws Exception;

    void collect(final TaskWork task, final Transport transport, final Context context) throws Exception;

    void analyse(final TaskWork task, final Transport transport, final Context context, final int timeout) throws Exception;
}
