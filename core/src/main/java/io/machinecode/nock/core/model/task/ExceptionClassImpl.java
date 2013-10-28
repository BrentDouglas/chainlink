package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.util.ResolvableClass;
import io.machinecode.nock.spi.element.task.ExceptionClass;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExceptionClassImpl implements ExceptionClass {

    private final ResolvableClass<? extends Throwable> clazz;

    public ExceptionClassImpl(final String fqcn) {
        this.clazz = new ResolvableClass<Throwable>(fqcn);
    }

    @Override
    public String getClassName() {
        return this.clazz.fqcn();
    }

    public boolean matches(final Class<?> theirs, final ClassLoader loader) throws ClassNotFoundException {
        return theirs.getCanonicalName().equals(getClassName())
                || this.clazz.resolve(loader).isAssignableFrom(theirs);
    }
}
