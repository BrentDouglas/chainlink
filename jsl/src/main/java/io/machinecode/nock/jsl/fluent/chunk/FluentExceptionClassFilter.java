package io.machinecode.nock.jsl.fluent.chunk;

import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentExceptionClassFilter implements ExceptionClassFilter {

    private final List<FluentExceptionClass> includes = new ArrayList<FluentExceptionClass>(0);
    private final List<FluentExceptionClass> excludes = new ArrayList<FluentExceptionClass>(0);

    @Override
    public List<FluentExceptionClass> getIncludes() {
        return this.includes;
    }

    public FluentExceptionClassFilter setIncludes(final String... includes) {
        for (final String include : includes) {
            this.includes.add(new FluentExceptionClass().setClassName(include));
        }
        return this;
    }

    public FluentExceptionClassFilter setIncludes(final Class<? extends Throwable>... includes) {
        for (final Class<? extends Throwable> include : includes) {
            this.includes.add(new FluentExceptionClass().setClassName(include.getCanonicalName()));
        }
        return this;
    }

    public FluentExceptionClassFilter setInclude(final String include) {
        this.includes.add(new FluentExceptionClass().setClassName(include));
        return this;
    }

    public FluentExceptionClassFilter setInclude(final Class<? extends Throwable> include) {
        this.includes.add(new FluentExceptionClass().setClassName(include.getCanonicalName()));
        return this;
    }

    @Override
    public List<FluentExceptionClass> getExcludes() {
        return this.excludes;
    }

    public FluentExceptionClassFilter setExcludes(final String... excludes) {
        for (final String exclude : excludes) {
            this.excludes.add(new FluentExceptionClass().setClassName(exclude));
        }
        return this;
    }

    public FluentExceptionClassFilter setExcludes(final Class<? extends Throwable>... excludes) {
        for (final Class<? extends Throwable> exclude : excludes) {
            this.excludes.add(new FluentExceptionClass().setClassName(exclude.getCanonicalName()));
        }
        return this;
    }

    public FluentExceptionClassFilter setExclude(final String exclude) {
        this.excludes.add(new FluentExceptionClass().setClassName(exclude));
        return this;
    }

    public FluentExceptionClassFilter setExclude(final Class<? extends Throwable> exclude) {
        this.excludes.add(new FluentExceptionClass().setClassName(exclude.getCanonicalName()));
        return this;
    }
}
