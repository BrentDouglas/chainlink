package io.machinecode.nock.jsl.impl.chunk;

import io.machinecode.nock.jsl.api.chunk.Classes;
import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterImpl implements ExceptionClassFilter {

    private final Classes includes;
    private final Classes excludes;

    public ExceptionClassFilterImpl(final ExceptionClassFilter that) {
        this.includes = new ClassesImpl(that.getIncludes());
        this.excludes = new ClassesImpl(that.getExcludes());
    }

    @Override
    public Classes getIncludes() {
        return this.includes;
    }

    @Override
    public Classes getExcludes() {
        return this.excludes;
    }
}
