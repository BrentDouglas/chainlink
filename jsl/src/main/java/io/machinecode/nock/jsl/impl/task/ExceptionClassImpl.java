package io.machinecode.nock.jsl.impl.task;

import io.machinecode.nock.jsl.api.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassImpl implements ExceptionClass {

    private final String className;

    public ExceptionClassImpl(final ExceptionClass that) {
        this.className = that.getClassName();
    }

    @Override
    public String getClassName() {
        return this.className;
    }
}
