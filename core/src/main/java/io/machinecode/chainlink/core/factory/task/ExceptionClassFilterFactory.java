package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.element.task.ExceptionClassFilterImpl;
import io.machinecode.chainlink.core.element.task.ExceptionClassImpl;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.util.Util;
import io.machinecode.chainlink.core.util.Util.ExpressionTransformer;
import io.machinecode.chainlink.spi.element.task.ExceptionClass;
import io.machinecode.chainlink.spi.element.task.ExceptionClassFilter;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExceptionClassFilterFactory implements ElementFactory<ExceptionClassFilter, ExceptionClassFilterImpl> {

    public static final ExceptionClassFilterFactory INSTANCE = new ExceptionClassFilterFactory();

    private static final ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext> EXCEPTION_CLASS_EXECUTION_TRANSFORMER = new ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final JobPropertyContext context) {
            return ExceptionClassFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ExceptionClassImpl, ExceptionClassImpl, PropertyContext> EXCEPTION_CLASS_PARTITION_TRANSFORMER = new ExpressionTransformer<ExceptionClassImpl, ExceptionClassImpl, PropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClassImpl that, final PropertyContext context) {
            return ExceptionClassFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ExceptionClassFilterImpl produceExecution(final ExceptionClassFilter that, final JobPropertyContext context) {
        final List<ExceptionClassImpl> includes = that == null ? Collections.<ExceptionClassImpl>emptyList() : Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_EXECUTION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = that == null ? Collections.<ExceptionClassImpl>emptyList() : Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_EXECUTION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }

    @Override
    public ExceptionClassFilterImpl producePartitioned(final ExceptionClassFilterImpl that, final PropertyContext context) {
        final List<ExceptionClassImpl> includes = Util.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Util.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }
}
