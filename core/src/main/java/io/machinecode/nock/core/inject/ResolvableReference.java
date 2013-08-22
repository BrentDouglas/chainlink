package io.machinecode.nock.core.inject;

import io.machinecode.nock.spi.inject.Resolvable;
import io.machinecode.nock.spi.inject.Resolver;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolvableReference<T> implements Resolvable<T> {

    private final String ref;
    private final ResolvableClass<T> clazz;

    public ResolvableReference(final String ref, final Class<T> clazz) {
        this.ref = ref;
        this.clazz = new ResolvableClass<T>(clazz);
    }

    @Override
    public T resolve(final ClassLoader loader) throws ClassNotFoundException {
        final ServiceLoader<Resolver> resolvers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<Resolver>>() {
            public ServiceLoader<Resolver> run() {
                return ServiceLoader.load(Resolver.class, loader);
            }
        });
        final Class<T> clazz = this.clazz.resolve(loader);
        for (final Resolver resolver : resolvers) {
            final T that = resolver.resolve(this.ref, clazz);
            if (that != null) {
                return that;
            }
        }
        return null;
    }

    public String ref() {
        return this.ref;
    }
}
