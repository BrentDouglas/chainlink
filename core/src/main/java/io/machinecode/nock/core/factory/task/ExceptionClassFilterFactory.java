package io.machinecode.nock.core.factory.task;

import io.machinecode.nock.core.model.task.ExceptionClassFilterImpl;
import io.machinecode.nock.core.model.task.ExceptionClassImpl;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.factory.ElementFactory;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.spi.element.task.ExceptionClass;
import io.machinecode.nock.spi.element.task.ExceptionClassFilter;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterFactory implements ElementFactory<ExceptionClassFilter, ExceptionClassFilterImpl> {

    public static final ExceptionClassFilterFactory INSTANCE = new ExceptionClassFilterFactory();

    private static final ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext> EXCEPTION_CLASS_EXECUTION_TRANSFORMER = new ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final JobPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ExceptionClassImpl, ExceptionClassImpl, PartitionPropertyContext> EXCEPTION_CLASS_PARTITION_TRANSFORMER = new ExpressionTransformer<ExceptionClassImpl, ExceptionClassImpl, PartitionPropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClassImpl that, final PartitionPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ExceptionClassFilterImpl produceExecution(final ExceptionClassFilter that, final JobPropertyContext context) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_EXECUTION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_EXECUTION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }

    @Override
    public ExceptionClassFilterImpl producePartitioned(final ExceptionClassFilterImpl that, final PartitionPropertyContext context) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }
}
