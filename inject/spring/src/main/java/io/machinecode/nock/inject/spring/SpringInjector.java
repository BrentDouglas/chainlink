package io.machinecode.nock.inject.spring;

import io.machinecode.nock.spi.extension.ContextProvider;
import io.machinecode.nock.spi.inject.Injector;
import io.machinecode.nock.spi.util.Pair;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.ReflectionUtils.FieldCallback;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringInjector implements Injector {

    private ContextProvider provider;

    public SpringInjector() {
        final ServiceLoader<ContextProvider> providers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<ContextProvider>>() {
            public ServiceLoader<ContextProvider> run() {
                return ServiceLoader.load(ContextProvider.class);
            }
        });
        final Iterator<ContextProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            provider = iterator.next();
        } else {
            throw new IllegalStateException(); //TODO Message
        }
    }

    @Override
    public <T> boolean inject(final T bean) throws Exception {
        final Class<?> clazz = bean.getClass();
        _context(clazz, JobContext.class, bean, new LazyGet<JobContext>() {
            @Override
            public JobContext get() {
                return provider.getJobContext();
            }
        });
        _context(clazz, StepContext.class, bean, new LazyGet<StepContext>() {
            @Override
            public StepContext get() {
                return provider.getStepContext();
            }
        });
        _batchProperty(clazz, String.class, bean, new LazyGet<List<? extends Pair<String, String>>>() {
            @Override
            public List<? extends Pair<String, String>> get() {
                return provider.getProperties();
            }
        });
        return true;
    }

    private interface LazyGet<T> {
        T get();
    }

    private static <T> void _context(final Class<?> beanClazz, final Class<?> targetClazz, final Object bean, final LazyGet<T> that) {
        ReflectionUtils.doWithFields(beanClazz,
                new FieldCallback() {
                    @Override
                    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                        if (that == null) {
                            return;
                        }
                        set(field, bean, that.get());
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches(final Field field) {
                        final int modifiers = field.getModifiers();
                        return !Modifier.isStatic(modifiers)
                                && !Modifier.isFinal(modifiers)
                                && field.getType().equals(targetClazz)
                                && field.isAnnotationPresent(Inject.class);
                    }
                }
        );
    }

    private static <T> void _batchProperty(final Class<?> beanClazz, final Class<T> targetClazz, final Object bean, final LazyGet<List<? extends Pair<String, String>>> properties) {
        ReflectionUtils.doWithFields(beanClazz,
                new FieldCallback() {
                    @Override
                    public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
                        final BatchProperty batchProperty = field.getAnnotation(BatchProperty.class);
                        final String property = _property(batchProperty.name(), field.getName(), properties.get());
                        if (property == null || "".equals(property)) {
                            return;
                        }
                        set(field, bean, property);
                    }
                },
                new ReflectionUtils.FieldFilter() {
                    @Override
                    public boolean matches(final Field field) {
                        return field.getType().equals(targetClazz)
                                && field.isAnnotationPresent(Inject.class)
                                && field.isAnnotationPresent(BatchProperty.class);
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

    private static void set(final Field field, final Object bean, final Object value) throws IllegalAccessException {
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                final boolean accessible = field.isAccessible();
                try {
                    field.setAccessible(true);
                    field.set(bean, value);
                } catch (final Exception e) {
                    //
                } finally {
                    field.setAccessible(accessible);
                }
                return null;
            }
        });
    }
}
