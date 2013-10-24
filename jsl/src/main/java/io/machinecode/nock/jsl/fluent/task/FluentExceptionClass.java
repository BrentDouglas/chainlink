package io.machinecode.nock.jsl.fluent.task;

import io.machinecode.nock.jsl.inherit.task.InheritableExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentExceptionClass implements InheritableExceptionClass<FluentExceptionClass> {

    private String className;

    @Override
    public String getClassName() {
        return this.className;
    }

    public FluentExceptionClass setClassName(final String className) {
        this.className = className;
        return this;
    }

    @Override
    public FluentExceptionClass copy() {
        return copy(new FluentExceptionClass());
    }

    @Override
    public FluentExceptionClass copy(final FluentExceptionClass that) {
        return ExceptionClassTool.copy(this, that);
    }

    @Override
    public FluentExceptionClass merge(final FluentExceptionClass that) {
        return ExceptionClassTool.merge(this, that);
    }
}
