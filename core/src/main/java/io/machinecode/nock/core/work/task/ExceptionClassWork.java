package io.machinecode.nock.core.work.task;

import io.machinecode.nock.core.inject.ResolvableClass;
import io.machinecode.nock.spi.element.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassWork implements ExceptionClass {

    private final ResolvableClass<? extends Throwable> clazz;

    public ExceptionClassWork(final String fqcn) {
        this.clazz = new ResolvableClass<Throwable>(fqcn);
    }

    @Override
    public String getClassName() {
        return this.clazz.fqcn();
    }
}
