package io.machinecode.nock.core.descriptor.task;

import io.machinecode.nock.spi.element.task.ExceptionClass;

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
