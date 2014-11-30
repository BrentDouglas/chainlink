package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.jsl.fluent.FluentMergeableList;
import io.machinecode.chainlink.jsl.core.inherit.task.InheritableExceptionClassFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FluentExceptionClassFilter
        extends FluentMergeableList<FluentExceptionClassFilter>
        implements InheritableExceptionClassFilter<FluentExceptionClassFilter, FluentExceptionClass> {

    private List<FluentExceptionClass> includes = new ArrayList<FluentExceptionClass>(0);
    private List<FluentExceptionClass> excludes = new ArrayList<FluentExceptionClass>(0);

    @Override
    public List<FluentExceptionClass> getIncludes() {
        return this.includes;
    }

    @Override
    public FluentExceptionClassFilter setIncludes(final List<FluentExceptionClass> includes) {
        this.includes = includes;
        return this;
    }

    public FluentExceptionClassFilter addIncludes(final String... includes) {
        for (final String include : includes) {
            this.includes.add(new FluentExceptionClass().setClassName(include));
        }
        return this;
    }

    public FluentExceptionClassFilter addIncludes(final Class<? extends Throwable>... includes) {
        for (final Class<? extends Throwable> include : includes) {
            this.includes.add(new FluentExceptionClass().setClassName(include.getCanonicalName()));
        }
        return this;
    }

    public FluentExceptionClassFilter addInclude(final String include) {
        this.includes.add(new FluentExceptionClass().setClassName(include));
        return this;
    }

    public FluentExceptionClassFilter addInclude(final Class<? extends Throwable> include) {
        this.includes.add(new FluentExceptionClass().setClassName(include.getCanonicalName()));
        return this;
    }

    @Override
    public List<FluentExceptionClass> getExcludes() {
        return this.excludes;
    }

    @Override
    public FluentExceptionClassFilter setExcludes(final List<FluentExceptionClass> excludes) {
        this.excludes = excludes;
        return this;
    }

    public FluentExceptionClassFilter addExcludes(final String... excludes) {
        for (final String exclude : excludes) {
            this.excludes.add(new FluentExceptionClass().setClassName(exclude));
        }
        return this;
    }

    public FluentExceptionClassFilter addExcludes(final Class<? extends Throwable>... excludes) {
        for (final Class<? extends Throwable> exclude : excludes) {
            this.excludes.add(new FluentExceptionClass().setClassName(exclude.getCanonicalName()));
        }
        return this;
    }

    public FluentExceptionClassFilter addExclude(final String exclude) {
        this.excludes.add(new FluentExceptionClass().setClassName(exclude));
        return this;
    }

    public FluentExceptionClassFilter addExclude(final Class<? extends Throwable> exclude) {
        this.excludes.add(new FluentExceptionClass().setClassName(exclude.getCanonicalName()));
        return this;
    }

    @Override
    public FluentExceptionClassFilter copy() {
        return copy(new FluentExceptionClassFilter());
    }

    @Override
    public FluentExceptionClassFilter copy(final FluentExceptionClassFilter that) {
        return ExceptionClassFilterTool.copy(this, that);
    }

    @Override
    public FluentExceptionClassFilter merge(final FluentExceptionClassFilter that) {
        return ExceptionClassFilterTool.merge(this, that);
    }
}
