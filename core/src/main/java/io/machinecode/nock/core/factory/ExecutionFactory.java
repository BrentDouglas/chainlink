package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.jsl.api.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutionFactory<T extends Execution, U extends T> {

    /**
     *
     * @param that
     * @param next
     * @param context
     */
    U produceBuildTime(T that, Execution next, JobPropertyContext context);

    /**
     *
     * @param that
     * @param context
     */
    U producePartitionTime(T that, PartitionPropertyContext context);
}
