package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.core.jsl.impl.task.ExceptionClassImpl;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExceptionClassFactory {

    public static ExceptionClassImpl produceExecution(final ExceptionClass that, final JobPropertyContext context) {
        final String className = Expression.resolveExecutionProperty(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }

    public static ExceptionClassImpl producePartitioned(final ExceptionClassImpl that, final PropertyContext context) {
        final String className = Expression.resolvePartitionProperty(that.getClassName(), context);
        return new ExceptionClassImpl(className);
    }
}
