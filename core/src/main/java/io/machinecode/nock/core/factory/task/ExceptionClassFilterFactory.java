package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.model.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.model.task.ExceptionClassImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.ParametersTransformer;
import io.machinecode.nock.jsl.api.task.ExceptionClass;
import io.machinecode.nock.jsl.api.task.ExceptionClassFilter;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterFactory implements ElementFactory<ExceptionClassFilter, ExceptionClassFilterImpl> {

    public static final ExceptionClassFilterFactory INSTANCE = new ExceptionClassFilterFactory();

    private static final ExpressionTransformer<ExceptionClass, ExceptionClassImpl> EXCEPTION_CLASS_BUILD_TRANSFORMER = new ExpressionTransformer<ExceptionClass, ExceptionClassImpl>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final JobPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.produceBuildTime(that, context);
        }
    };

    private static final ParametersTransformer<ExceptionClass, ExceptionClassImpl> EXCEPTION_CLASS_START_TRANSFORMER = new ParametersTransformer<ExceptionClass, ExceptionClassImpl>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final Properties parameters) {
            return ExceptionClassFactory.INSTANCE.produceStartTime(that, parameters);
        }
    };

    private static final ExpressionTransformer<ExceptionClass, ExceptionClassImpl> EXCEPTION_CLASS_PARTITION_TRANSFORMER = new ExpressionTransformer<ExceptionClass, ExceptionClassImpl>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final JobPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.producePartitionTime(that, context);
        }
    };

    @Override
    public ExceptionClassFilterImpl produceBuildTime(final ExceptionClassFilter that, final JobPropertyContext context) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_BUILD_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_BUILD_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }

    @Override
    public ExceptionClassFilterImpl produceStartTime(final ExceptionClassFilter that, final Properties parameters) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), parameters, EXCEPTION_CLASS_START_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), parameters, EXCEPTION_CLASS_START_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }

    @Override
    public ExceptionClassFilterImpl producePartitionTime(final ExceptionClassFilter that, final JobPropertyContext context) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }
}
