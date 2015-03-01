package io.machinecode.chainlink.core.factory.task;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PropertyContext;
import io.machinecode.chainlink.core.jsl.impl.task.ExceptionClassFilterImpl;
import io.machinecode.chainlink.core.jsl.impl.task.ExceptionClassImpl;
import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Copy.ExpressionTransformer;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClass;
import io.machinecode.chainlink.spi.jsl.task.ExceptionClassFilter;

import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExceptionClassFilterFactory {

    private static final ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext> EXCEPTION_CLASS_EXECUTION_TRANSFORMER = new ExpressionTransformer<ExceptionClass, ExceptionClassImpl, JobPropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClass that, final JobPropertyContext context) {
            return ExceptionClassFactory.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ExceptionClassImpl, ExceptionClassImpl, PropertyContext> EXCEPTION_CLASS_PARTITION_TRANSFORMER = new ExpressionTransformer<ExceptionClassImpl, ExceptionClassImpl, PropertyContext>() {
        @Override
        public ExceptionClassImpl transform(final ExceptionClassImpl that, final PropertyContext context) {
            return ExceptionClassFactory.producePartitioned(that, context);
        }
    };

    public static ExceptionClassFilterImpl produceExecution(final ExceptionClassFilter that, final JobPropertyContext context) {
        final List<ExceptionClassImpl> includes = that == null ? Collections.<ExceptionClassImpl>emptyList() : Copy.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_EXECUTION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = that == null ? Collections.<ExceptionClassImpl>emptyList() : Copy.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_EXECUTION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }

    public static ExceptionClassFilterImpl producePartitioned(final ExceptionClassFilterImpl that, final PropertyContext context) {
        final List<ExceptionClassImpl> includes = Copy.immutableCopy(that.getIncludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        final List<ExceptionClassImpl> excludes = Copy.immutableCopy(that.getExcludes(), context, EXCEPTION_CLASS_PARTITION_TRANSFORMER);
        return new ExceptionClassFilterImpl(includes, excludes);
    }
}
