package io.machinecode.nock.jsl.impl.chunk;

import io.machinecode.nock.jsl.api.chunk.ExceptionClass;
import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;
import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.impl.Util.Transformer;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterImpl implements ExceptionClassFilter {

    private static final Transformer<ExceptionClass> EXCEPTION_CLASS_TRANSFORMER = new Transformer<ExceptionClass>() {
        @Override
        public ExceptionClass transform(final ExceptionClass that) {
            return new ExceptionClassImpl(that);
        }
    };

    private final List<ExceptionClass> includes;
    private final List<ExceptionClass> excludes;

    public ExceptionClassFilterImpl(final ExceptionClassFilter that) {
        if (that == null) {
            this.includes = Collections.emptyList();
            this.excludes = Collections.emptyList();
        } else {
            this.includes = Util.immutableCopy(that.getIncludes(), EXCEPTION_CLASS_TRANSFORMER);
            this.excludes = Util.immutableCopy(that.getExcludes(), EXCEPTION_CLASS_TRANSFORMER);
        }
    }

    @Override
    public List<ExceptionClass> getIncludes() {
        return this.includes;
    }

    @Override
    public List<ExceptionClass> getExcludes() {
        return this.excludes;
    }
}
