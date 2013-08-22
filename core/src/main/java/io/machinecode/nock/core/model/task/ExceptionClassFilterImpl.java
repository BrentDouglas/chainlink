package io.machinecode.nock.core.model.task;

import io.machinecode.nock.spi.element.task.ExceptionClassFilter;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterImpl implements ExceptionClassFilter {

    private final List<ExceptionClassImpl> includes;
    private final List<ExceptionClassImpl> excludes;

    public ExceptionClassFilterImpl(final List<ExceptionClassImpl> includes, final List<ExceptionClassImpl> excludes) {
        this.includes = includes == null ? Collections.<ExceptionClassImpl>emptyList() : includes;
        this.excludes = excludes == null ? Collections.<ExceptionClassImpl>emptyList() : excludes;
    }

    @Override
    public List<ExceptionClassImpl> getIncludes() {
        return this.includes;
    }

    @Override
    public List<ExceptionClassImpl> getExcludes() {
        return this.excludes;
    }
}
