package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.jsl.api.Element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ElementFactory<T extends Element, U extends T> {

    /**
     *
     * @param that
     * @param context
     */
    U produceBuildTime(T that, JobPropertyContext context);

    /**
     *
     * @param that
     * @param context
     */
    U producePartitionTime(T that, PartitionPropertyContext context);
}
