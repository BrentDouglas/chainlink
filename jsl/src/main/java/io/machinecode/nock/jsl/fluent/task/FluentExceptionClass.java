package io.machinecode.nock.jsl.fluent.task;

import io.machinecode.nock.spi.element.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentExceptionClass implements ExceptionClass {

    private String className;

    @Override
    public String getClassName() {
        return this.className;
    }

    public FluentExceptionClass setClassName(final String className) {
        this.className = className;
        return this;
    }
}
