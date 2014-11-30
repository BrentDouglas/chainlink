package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.util.Resolvable;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ResolvableClass<T> implements Resolvable<Class<T>>, Serializable {

    private final String fqcn;
    private transient Class<T> clazz;

    public ResolvableClass(final String fqcn) {
        this.fqcn = fqcn;
    }

    public ResolvableClass(final Class<T> clazz) {
        this.fqcn = clazz.getCanonicalName();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<T> resolve(final ClassLoader loader) throws ClassNotFoundException {
        if (clazz == null) {
            clazz = (Class<T>) loader.loadClass(this.fqcn);
        }
        return clazz;
    }

    public String fqcn() {
        return this.fqcn;
    }
}
