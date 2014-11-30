package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.spi.element.Listeners;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.task.Task;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
