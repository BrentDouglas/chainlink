package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.util.ResolvableClass;
import io.machinecode.chainlink.spi.element.task.ExceptionClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
