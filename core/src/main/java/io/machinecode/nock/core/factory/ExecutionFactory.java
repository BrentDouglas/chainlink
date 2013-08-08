package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.jsl.api.execution.Execution;

import java.util.Properties;

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
     * @param parameters
     */
    U produceStartTime(T that, Properties parameters);

    /**
     *
     * @param that
     * @param context
     */
    U producePartitionTime(T that, JobPropertyContext context);
}
