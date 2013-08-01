package io.machinecode.nock.jsl.fluent.chunk;

import io.machinecode.nock.jsl.api.chunk.ExceptionClass;

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
