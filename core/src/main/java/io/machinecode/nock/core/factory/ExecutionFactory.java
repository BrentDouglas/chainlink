package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionFactory<T extends Execution, U extends T, V> {

    /**
     *
     * @param that
     * @param execution
     * @param context
     */
    U produceDescriptor(T that, Execution execution, JobPropertyContext context);

    /**
     *
     * @param that
     * @param execution
     * @param context
     */
    V produceExecution(U that, Execution execution, JobParameterContext context);

    /**
     *
     * @param that
     * @param context
     */
    V producePartitioned(V that, Execution execution, PartitionPropertyContext context);
}
