package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.transport.Transport;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Worker extends Serializable {

    Future<Void> runJob(final JobWork work, final Transport transport, final Context context);

    Future<Void> runExecution(final ExecutionWork work, final Transport transport, final Context context) throws Exception;

    Future<Void> runPartition(final PartitionWork work, final Transport transport, final Context context) throws Exception;

    ExecutionWork transitionOrSetStatus(final Transport transport, final Context context, final List<? extends TransitionWork> transitions, final String next) throws Exception;
}
