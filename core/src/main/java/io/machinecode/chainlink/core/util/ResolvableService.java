package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.spi.util.Resolvable;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ResolvableService<T> implements Resolvable<List<T>> {

    private final String property;
    private final ResolvableClass<T> clazz;

    public ResolvableService(final Class<T> clazz) {
        this(null, clazz);
    }

    public ResolvableService(final String property, final Class<T> clazz) {
        this.property = property;
        this.clazz = new ResolvableClass<T>(clazz);
    }

    @Override
    public List<T> resolve(final ClassLoader loader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (this.property != null) {
            final String name = AccessController.doPrivileged(new PrivilegedAction<String>() {
                @Override
                public String run() {
                    return System.getProperty(ResolvableService.this.property);
                }
            });
            if (name != null) {
                return Collections.singletonList((T) loader.loadClass(name).newInstance());
            }
        }
        final Class<T> clazz = this.clazz.resolve(loader);
        final ServiceLoader<T> resolvers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<T>>() {
            public ServiceLoader<T> run() {
                return ServiceLoader.load(clazz, loader);
            }
        });
        final List<T> services = new ArrayList<T>();
        for (final T that : resolvers) {
            services.add(that);
        }
        return services;
    }
}
