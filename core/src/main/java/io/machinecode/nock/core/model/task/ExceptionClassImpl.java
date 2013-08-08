package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassImpl implements ExceptionClass {

    private final String className;

    public ExceptionClassImpl(final String className) {
        this.className = className;
    }

    @Override
    public String getClassName() {
        return this.className;
    }
}
