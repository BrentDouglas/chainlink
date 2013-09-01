package io.machinecode.nock.core.util;

import io.machinecode.nock.spi.util.Resolvable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolvableClass<T> implements Resolvable<Class<T>> {

    private final String fqcn;

    public ResolvableClass(final String fqcn) {
        this.fqcn = fqcn;
    }

    public ResolvableClass(final Class<T> clazz) {
        this.fqcn = clazz.getCanonicalName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> resolve(final ClassLoader loader) throws ClassNotFoundException {
        return (Class<T>) loader.loadClass(this.fqcn);
    }

    public String fqcn() {
        return this.fqcn;
    }
}
