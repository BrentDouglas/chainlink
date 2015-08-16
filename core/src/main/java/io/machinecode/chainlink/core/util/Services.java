/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
