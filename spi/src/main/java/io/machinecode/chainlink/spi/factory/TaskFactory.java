package io.machinecode.chainlink.spi.factory;

import io.machinecode.chainlink.spi.element.Listeners;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.task.Task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TaskFactory<T extends Task, U extends T, V extends Listeners, X extends Partition> {

    /**
     *
     * @param that
     * @param listeners
     * @param context
     */
    U produceExecution(T that, V listeners, X partition, JobPropertyContext context);

    /**
     *
     * @param that
     * @param listeners
     * @param context
     */
    U producePartitioned(U that, V listeners, X partition, PropertyContext context);
}
