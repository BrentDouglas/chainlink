package io.machinecode.chainlink.core.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Services {

    public static <T> List<T> load(final Class<T> clazz, final ClassLoader loader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return load(null, clazz, loader);
    }

    public static <T> List<T> load(final String property, final Class<T> clazz, final ClassLoader loader) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (property != null) {
            final String name = AccessController.doPrivileged(new LoadProperty(property));
            if (name != null) {
                return Collections.singletonList((T) loader.loadClass(name).newInstance());
            }
        }
        final ServiceLoader<T> resolvers = AccessController.doPrivileged(new LoadServices<>(clazz, loader));
        final List<T> services = new ArrayList<>();
        for (final T that : resolvers) {
            services.add(that);
        }
        return services;
    }

    private static class LoadProperty implements PrivilegedAction<String> {
        private final String property;

        private LoadProperty(final String property) {
            this.property = property;
        }

        @Override
        public String run() {
            return System.getProperty(property);
        }
    }

    private static class LoadServices<T> implements PrivilegedAction<ServiceLoader<T>> {
        private final Class<T> clazz;
        private final ClassLoader loader;

        public LoadServices(final Class<T> clazz, final ClassLoader loader) {
            this.clazz = clazz;
            this.loader = loader;
        }

        public ServiceLoader<T> run() {
            return ServiceLoader.load(clazz, loader);
        }
    }
}
