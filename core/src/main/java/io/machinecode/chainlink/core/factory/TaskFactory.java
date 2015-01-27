package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.jsl.Listeners;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.task.Task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
