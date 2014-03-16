package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.inject.core.DefaultInjector;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import java.lang.reflect.Member;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiInjector implements Injector {

    private final InjectablesProvider provider;

    public CdiInjector() {
        final ServiceLoader<InjectablesProvider> providers = AccessController.doPrivileged(new PrivilegedAction<ServiceLoader<InjectablesProvider>>() {
            public ServiceLoader<InjectablesProvider> run() {
                return ServiceLoader.load(InjectablesProvider.class);
            }
        });
        final Iterator<InjectablesProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            provider = iterator.next();
        } else {
            throw new IllegalStateException(Messages.format("CHAINLINK-000000.injector.provider.unavailable"));
        }
    }

    // TODO Returning false makes the default injector override any previously injected values
    // Need to return (bean instanceof CDI Proxy)
    @Override
    public boolean inject(final Object bean) throws Exception {
        return false;
    }

    @Produces
    @Dependent
    @BatchProperty
    public String getBatchProperty(final InjectionPoint injectionPoint) {
        final BatchProperty batchProperty = injectionPoint.getAnnotated().getAnnotation(BatchProperty.class);
        final Member field = injectionPoint.getMember();
        final String property = DefaultInjector.property(batchProperty.name(), field.getName(), provider.getInjectables().getProperties());
        if (property == null || "".equals(property)) {
            return null;
        }
        return property;
    }

    @Produces
    @Dependent
    @Default
    public JobContext getJobContext() {
        return provider.getInjectables().getJobContext();
    }

    @Produces
    @Dependent
    @Default
    public StepContext getStepContext() {
        return provider.getInjectables().getStepContext();
    }
}
