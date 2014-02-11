package io.machinecode.nock.spi.work;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.execution.CallbackExecutable;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.execution.Item;

import javax.batch.runtime.BatchStatus;
import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PartitionWork<T extends Strategy> extends Partition<T>, Serializable {

    PartitionTarget map(final TaskWork task, final Executor executor, final CallbackExecutable thisExecutable, final ExecutionContext context, final int timeout) throws Exception;

    Item collect(final TaskWork task, final Executor executor, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception;

    void analyse(final TaskWork task, final Executor executor, final ExecutionContext context, final int timeout, final List<Item> items) throws Exception;
}
