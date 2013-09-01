package io.machinecode.nock.spi.factory;

import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionFactory<T extends Execution, U extends T> {

    /**
     *
     * @param that
     * @param execution
     * @param context
     */
    U produceExecution(T that, Execution execution, JobPropertyContext context);

    /**
     *
     * @param that
     * @param context
     */
    U producePartitioned(U that, Execution execution, PropertyContext context);
}
