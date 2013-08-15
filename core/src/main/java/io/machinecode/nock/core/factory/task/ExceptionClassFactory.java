package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.task.ExceptionClassImpl;
import io.machinecode.nock.jsl.api.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFactory implements ElementFactory<ExceptionClass, ExceptionClassImpl> {

    public static final ExceptionClassFactory INSTANCE = new ExceptionClassFactory();

    @Override
    public ExceptionClassImpl produceBuildTime(final ExceptionClass that, final JobPropertyContext context) {
        final String className = Expression.resolveBuildTime(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }

    @Override
    public ExceptionClassImpl producePartitionTime(final ExceptionClass that, final PartitionPropertyContext context) {
        final String className = Expression.resolvePartition(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }
}
