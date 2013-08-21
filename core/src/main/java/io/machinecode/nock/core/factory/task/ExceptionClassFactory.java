package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.task.ExceptionClassImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.work.task.ExceptionClassWork;
import io.machinecode.nock.spi.element.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFactory implements ElementFactory<ExceptionClass, ExceptionClassImpl, ExceptionClassWork> {

    public static final ExceptionClassFactory INSTANCE = new ExceptionClassFactory();

    @Override
    public ExceptionClassImpl produceDescriptor(final ExceptionClass that, final JobPropertyContext context) {
        final String className = Expression.resolveDescriptorProperty(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }

    @Override
    public ExceptionClassWork produceExecution(final ExceptionClassImpl that, final JobParameterContext context) {
        final String className = Expression.resolveExecutionProperty(that.getClassName(), context);
        return new ExceptionClassWork(className);
    }

    @Override
    public ExceptionClassWork producePartitioned(final ExceptionClassWork that, final PartitionPropertyContext context) {
        final String className = Expression.resolvePartitionProperty(that.getClassName(), context);
        return new ExceptionClassWork(className);
    }
}
