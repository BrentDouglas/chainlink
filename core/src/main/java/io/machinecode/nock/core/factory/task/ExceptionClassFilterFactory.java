package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.descriptor.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.descriptor.task.ExceptionClassImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.work.task.ExceptionClassFilterWork;
import io.machinecode.nock.core.work.task.ExceptionClassWork;
import io.machinecode.nock.spi.element.task.ExceptionClass;
import io.machinecode.nock.spi.element.task.ExceptionClassFilter;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterFactory implements ElementFactory<ExceptionClassFilter, ExceptionClassFilterImpl, ExceptionClassFilterWork> {

    public static final ExceptionClassFilterFactory INSTANCE = new ExceptionClassFilterFactory();

    private static final ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext> EXCEPTION_CLASS_BUILD_TRANSFORMER = new ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final JobPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.produceDescriptor(that, context);
        }
    };

    private static final ExpressionTransformer<ExceptionClassImpl, ExceptionClassWork, JobParameterContext> EXCEPTION_CLASS_EXCEPTION_TRANSFORMER = new ExpressionTransformer<ExceptionClassImpl, ExceptionClassWork, JobParameterContext>() {
        @Override
        public ExceptionClassWork transform(final ExceptionClassImpl that, final JobParameterContext context) {
            return ExceptionClassFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ExceptionClassWork, ExceptionClassWork, PartitionPropertyContext> EXCEPTION_CLASS_PARTITION_TRANSFORMER = new ExpressionTransformer<ExceptionClassWork, ExceptionClassWork, PartitionPropertyContext>() {
        @Override
        public ExceptionClassWork transform(final ExceptionClassWork that, final PartitionPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ExceptionClassFilterImpl produceDescriptor(final ExceptionClassFilter that, final JobPropertyContext context) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_BUILD_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_BUILD_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }

    @Override
    public ExceptionClassFilterWork produceExecution(final ExceptionClassFilterImpl that, final JobParameterContext context) {
        final List<ExceptionClassWork> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_EXCEPTION_TRANSFORMER);
        final List<ExceptionClassWork> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_EXCEPTION_TRANSFORMER);
        return new ExceptionClassFilterWork(
                includes,
                excludes
        );
    }

    @Override
    public ExceptionClassFilterWork producePartitioned(final ExceptionClassFilterWork that, final PartitionPropertyContext context) {
        final List<ExceptionClassWork> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        final List<ExceptionClassWork> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        return new ExceptionClassFilterWork(includes, excludes);
    }
}
