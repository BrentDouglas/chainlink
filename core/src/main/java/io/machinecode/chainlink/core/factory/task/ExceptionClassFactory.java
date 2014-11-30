package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.element.task.ExceptionClassImpl;
import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.spi.element.task.ExceptionClass;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ExceptionClassFactory implements ElementFactory<ExceptionClass, ExceptionClassImpl> {

    public static final ExceptionClassFactory INSTANCE = new ExceptionClassFactory();

    @Override
    public ExceptionClassImpl produceExecution(final ExceptionClass that, final JobPropertyContext context) {
        final String className = Expression.resolveExecutionProperty(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }

    @Override
    public ExceptionClassImpl producePartitioned(final ExceptionClassImpl that, final PropertyContext context) {
        final String className = Expression.resolvePartitionProperty(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }
}
