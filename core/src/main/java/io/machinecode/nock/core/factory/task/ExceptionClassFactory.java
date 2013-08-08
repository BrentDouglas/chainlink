package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.task.ExceptionClassImpl;
import io.machinecode.nock.jsl.api.task.ExceptionClass;
import io.machinecode.nock.jsl.util.MutablePair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFactory implements ElementFactory<ExceptionClass, ExceptionClassImpl> {

    public static final ExceptionClassFactory INSTANCE = new ExceptionClassFactory();

    @Override
    public ExceptionClassImpl produceBuildTime(final ExceptionClass that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> jobProperties = context.getProperties();
        final String className = Expression.resolveBuildTime(that.getClassName(), jobProperties);
        return new ExceptionClassImpl(className);
    }

    @Override
    public ExceptionClassImpl produceStartTime(final ExceptionClass that, final Properties parameters) {
        final String className = Expression.resolveStartTime(that.getClassName(), parameters);
        return new ExceptionClassImpl(className);
    }

    @Override
    public ExceptionClassImpl producePartitionTime(final ExceptionClass that, final JobPropertyContext context) {
        final List<MutablePair<String,String>> partitionPlan = context.getProperties();
        final String className = Expression.resolvePartition(that.getClassName(), partitionPlan);
        return new ExceptionClassImpl(className);
    }
}
