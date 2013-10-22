package io.machinecode.nock.inject.spring;

import io.machinecode.nock.spi.extension.ContextProvider;
import io.machinecode.nock.spi.util.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;
import org.springframework.util.ReflectionUtils.MethodCallback;
import org.springframework.util.ReflectionUtils.MethodFilter;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringProducer implements BeanPostProcessor {

    private ContextProvider provider;

    public SpringProducer() {
        final ServiceLoader<ContextProvider> providers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<ContextProvider>>() {
            public ServiceLoader<ContextProvider> run() {
                return ServiceLoader.load(ContextProvider.class);
            }
        });
        final Iterator<ContextProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            provider = iterator.next();
        } else {
            //throw new IllegalStateException(); //TODO Message
            provider = new ContextProvider() {
                @Override
                public JobContext getJobContext() {
                    return null;
                }

                @Override
                public StepContext getStepContext() {
                    return null;
                }

                @Override
                public List<? extends Pair<String, String>> getProperties() {
                    return Collections.emptyList();
                }
            };
        }
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        final Class<?> clazz = bean.getClass();
        _context(clazz, JobContext.class, bean, provider.getJobContext());
        _context(clazz, StepContext.class, bean, provider.getStepContext());
        _batchProperty(clazz, String.class, bean, provider.getProperties());
        return bean;
    }

    private static <T> void _context(final Class<?> beanClazz, final Class<?> targetClazz, final Object bean, final T that) {
        ReflectionUtils.doWithFields(beanClazz,
                new FieldCallback() {
                    @Override
                    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                        field.set(bean, that);
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches(final Field field) {
                        return field.getType().equals(targetClazz) && field.getAnnotation(Inject.class) != null;
                    }
                }
        );
        ReflectionUtils.doWithMethods(beanClazz,
                new MethodCallback() {
                    @Override
                    public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
                        try {
                            method.invoke(bean, that);
                        } catch (final InvocationTargetException e) {
                            throw new RuntimeException(e); //TODO Message
                        }
                    }
                },
                new MethodFilter() {
                    @Override
                    public boolean matches(final Method method) {
                        final Class<?>[] params = method.getParameterTypes();
                        return method.getAnnotation(Inject.class) != null
                                && params.length == 1
                                && params[0].equals(targetClazz);
                    }
                }
        );
    }

    private static <T> void _batchProperty(final Class<?> beanClazz, final Class<T> targetClazz, final Object bean, final List<? extends Pair<String, String>> properties) {
        ReflectionUtils.doWithFields(beanClazz,
                new FieldCallback() {
                    @Override
                    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                        final String batchProperty = field.getAnnotation(BatchProperty.class).name();
                        field.set(bean, _property(batchProperty, field.getName(), properties));
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches(final Field field) {
                        return field.getType().equals(targetClazz)
                                && field.getAnnotation(Inject.class) != null
                                && field.getAnnotation(BatchProperty.class) != null;
                    }
                }
        );
        ReflectionUtils.doWithMethods(beanClazz,
                new MethodCallback() {
                    @Override
                    public void doWith(final Method method) throws IllegalArgumentException, IllegalAccessException {
                        final String batchProperty = method.getAnnotation(BatchProperty.class).name();
                        try {
                            method.invoke(bean, _property(batchProperty, method.getName(), properties)); //TODO Needs to munge method.getName into EL name
                        } catch (final InvocationTargetException e) {
                            throw new RuntimeException(e); //TODO Message
                        }
                    }
                },
                new MethodFilter() {
                    @Override
                    public boolean matches(final Method method) {
                        final Class<?>[] params = method.getParameterTypes();
                        return method.getAnnotation(Inject.class) != null
                                && method.getAnnotation(BatchProperty.class) != null
                                && params.length == 1
                                && params[0].equals(targetClazz);
                    }
                }
        );
    }

    private static String _property(final String batchProperty, final String defaultName, final List<? extends Pair<String, String>> properties) {
        final String name;
        if ("".equals(batchProperty)) {
            name = defaultName;
        } else {
            name = batchProperty;
        }
        final ListIterator<? extends Pair<String, String>> iterator = properties.listIterator(properties.size());
        while (iterator.hasPrevious()) {
            final Pair<String, String> pair = iterator.previous();
            if (name.equals(pair.getKey())) {
                return pair.getValue();
            }
        }
        return null;
    }
}
