package io.machinecode.nock.jsl.fluent.chunk;

import io.machinecode.nock.jsl.api.chunk.Classes;
import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentExceptionClassFilter implements ExceptionClassFilter {

    private final FluentClasses includes = new FluentClasses();
    private final FluentClasses excludes = new FluentClasses();

    @Override
    public Classes getIncludes() {
        return this.includes;
    }

    public FluentExceptionClassFilter setIncludes(final String... includes) {
        this.includes.addClasses(includes);
        return this;
    }

    public FluentExceptionClassFilter setIncludes(final Class<? extends Throwable>... includes) {
        for (final Class<? extends Throwable> include : includes) {
            this.includes.addClass(include.getSimpleName());
        }
        return this;
    }

    public FluentExceptionClassFilter setInclude(final String include) {
        this.includes.addClass(include);
        return this;
    }

    public FluentExceptionClassFilter setInclude(final Class<? extends Throwable> include) {
        this.includes.addClass(include.getSimpleName());
        return this;
    }

    @Override
    public Classes getExcludes() {
        return this.excludes;
    }

    public FluentExceptionClassFilter setExcludes(final String... excludes) {
        this.excludes.addClasses(excludes);
        return this;
    }

    public FluentExceptionClassFilter setExcludes(final Class<? extends Throwable>... excludes) {
        for (final Class<? extends Throwable> exclude : excludes) {
            this.excludes.addClass(exclude.getSimpleName());
        }
        return this;
    }

    public FluentExceptionClassFilter setExclude(final String exclude) {
        this.excludes.addClass(exclude);
        return this;
    }

    public FluentExceptionClassFilter setExclude(final Class<? extends Throwable> exclude) {
        this.excludes.addClass(exclude.getSimpleName());
        return this;
    }
}
