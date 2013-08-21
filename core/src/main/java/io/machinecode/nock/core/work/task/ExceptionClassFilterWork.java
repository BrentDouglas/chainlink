package io.machinecode.nock.core.work.task;

import io.machinecode.nock.spi.element.task.ExceptionClassFilter;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassFilterWork implements ExceptionClassFilter {

    private final List<ExceptionClassWork> includes;
    private final List<ExceptionClassWork> excludes;

    public ExceptionClassFilterWork(final List<ExceptionClassWork> includes, final List<ExceptionClassWork> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    @Override
    public List<ExceptionClassWork> getIncludes() {
        return this.includes;
    }

    @Override
    public List<ExceptionClassWork> getExcludes() {
        return this.excludes;
    }
}
