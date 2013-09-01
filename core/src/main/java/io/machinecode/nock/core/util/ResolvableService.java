package io.machinecode.nock.core.util;

import io.machinecode.nock.spi.util.Resolvable;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResolvableService<T> implements Resolvable<List<T>> {

    private final ResolvableClass<T> clazz;

    public ResolvableService(final Class<T> clazz) {
        this.clazz = new ResolvableClass<T>(clazz);
    }

    @Override
    public List<T> resolve(final ClassLoader loader) throws ClassNotFoundException {
        final Class<T> clazz = this.clazz.resolve(loader);
        final ServiceLoader<T> resolvers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() {
            public ServiceLoader<T> run() {
                return ServiceLoader.load(clazz, loader);
            }
        });
        final ArrayList<T> list = new ArrayList<T>();
        for (final T that : resolvers) {
            list.add(that);
        }
        return list;
    }
}
