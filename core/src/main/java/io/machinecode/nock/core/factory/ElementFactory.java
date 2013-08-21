package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.spi.element.Element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ElementFactory<T extends Element, U extends T, V> {

    /**
     *
     * @param that
     * @param context
     */
    U produceDescriptor(T that, JobPropertyContext context);

    /**
     *
     * @param that
     * @param context
     */
    V produceExecution(U that, JobParameterContext context);

    /**
     *
     * @param that
     * @param context
     */
    V producePartitioned(V that, PartitionPropertyContext context);
}
