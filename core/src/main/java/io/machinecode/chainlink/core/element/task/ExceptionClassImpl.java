package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.util.ResolvableClass;
import io.machinecode.chainlink.spi.element.task.ExceptionClass;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExceptionClassImpl implements ExceptionClass, Serializable {
    private static final long serialVersionUID = 1L;

    private final ResolvableClass<? extends Throwable> clazz;

    public ExceptionClassImpl(final String fqcn) {
        this.clazz = new ResolvableClass<>(fqcn);
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
