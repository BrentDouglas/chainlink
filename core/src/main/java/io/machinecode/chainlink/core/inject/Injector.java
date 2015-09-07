/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.util.Pair;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import java.util.ListIterator;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Injector {

    public static  boolean inject(final Injectables injectables, final Object bean) throws Exception {
        Class<?> clazz = bean.getClass();
        do {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    final int modifiers = field.getModifiers();
                    final BatchProperty batchProperty = field.getAnnotation(BatchProperty.class);
                    if (String.class.equals(field.getType())) {
                        final String property = property(batchProperty.name(), field.getName(), injectables.getProperties());
                        if (property == null || "".equals(property)) {
                            continue;
                        }
                        set(field, bean, property);
                    } else if (JobContext.class.equals(field.getType())) {
                        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                            continue;
                        }
                        final JobContext jobContext = injectables.getJobContext();
                        if (jobContext == null) {
                            continue;
                        }
                        set(field, bean, jobContext);
                    } else if (StepContext.class.equals(field.getType())) {
                        if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                            continue;
                        }
                        final StepContext stepContext = injectables.getStepContext();
                        if (stepContext == null) {
                            continue;
                        }
                        set(field, bean, stepContext);
                    }
                }
            }
        } while ((clazz = clazz.getSuperclass()) != Object.class && clazz != null);
        return true;
    }

    public static String property(final String batchProperty, final String defaultName, final List<? extends Pair<String, String>> properties) {
        final String name;
        if ("".equals(batchProperty)) {
            name = defaultName;
        } else {
            name = batchProperty;
        }
        final ListIterator<? extends Pair<String, String>> iterator = properties.listIterator(properties.size());
        while (iterator.hasPrevious()) {
            final Pair<String, String> pair = iterator.previous();
            if (name.equals(pair.getName())) {
                return pair.getValue();
            }
        }
        return null;
    }

    public static void set(final Field field, final Object bean, final Object value) throws Exception {
        final Exception exception = AccessController.doPrivileged(new PrivilegedAction<Exception>() {
            @Override
            public Exception run() {
                final boolean accessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                    return null;
                } catch (final Exception e) {
                    return e;
                } finally {
                    field.setAccessible(accessible);
                }
            }
        });
        if (exception != null) {
            throw exception;
        }
    }

}
