package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.spi.element.Element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ElementFactory<T extends Element, U extends T> {

    /**
     *
     * @param that
     * @param context
     */
    U produceExecution(T that, JobPropertyContext context);

    /**
     *
     * @param that
     * @param context
     */
    U producePartitioned(U that, PartitionPropertyContext context);
}
