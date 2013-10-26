package io.machinecode.nock.core.inject;

import io.machinecode.nock.spi.inject.Injectables;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.Injector;
import io.machinecode.nock.spi.util.Pair;

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
public class DefaultInjector implements Injector {

    private InjectablesProvider provider;

    public DefaultInjector() {
        final ServiceLoader<InjectablesProvider> providers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<InjectablesProvider>>() {
            public ServiceLoader<InjectablesProvider> run() {
                return ServiceLoader.load(InjectablesProvider.class);
            }
        });
        final Iterator<InjectablesProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            provider = iterator.next();
        } else {
            throw new IllegalStateException(); //TODO Message
        }
    }


    @Override
    public <T> boolean inject(final T bean) throws Exception {
        Class<?> clazz = bean.getClass();
        final Injectables injectables = provider.getInjectables();
        do {
            for (final Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Inject.class)) {
                    final int modifiers = field.getModifiers();
                    final BatchProperty batchProperty = field.getAnnotation(BatchProperty.class);
                    if (String.class.equals(field.getType())) {
                        final String property = _property(batchProperty.name(), field.getName(), injectables.getProperties());
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
        } while ((clazz = clazz.getSuperclass()) != Object.class); //TODO Spec doesn't say if we should or not
        return true;
    }

    public static String _property(final String batchProperty, final String defaultName, final List<? extends Pair<String, String>> properties) {
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
