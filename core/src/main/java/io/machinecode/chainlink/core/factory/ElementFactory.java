package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.spi.element.Element;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

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
    U producePartitioned(U that, PropertyContext context);
}
