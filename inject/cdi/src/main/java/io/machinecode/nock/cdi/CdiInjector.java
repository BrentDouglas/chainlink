package io.machinecode.nock.cdi;

import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.Injector;
import io.machinecode.nock.spi.util.Pair;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
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

    private InjectablesProvider provider;

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
            throw new IllegalStateException(); //TODO Message
        }
    }

    // TODO Returning false makes the default injector override any previously injected values
    // Need to return (bean instanceof CDI Proxy)
    @Override
    public <T> boolean inject(final T bean) throws Exception {
        return false;
    }

    @Produces
    @Dependent
    @BatchProperty
    public String getBatchProperty(final InjectionPoint injectionPoint) {
        final String batchProperty = injectionPoint.getAnnotated().getAnnotation(BatchProperty.class).name();
        final String name;
        if (BatchPropertyLiteral.DEFAULT_NAME.equals(batchProperty)) {
            name = injectionPoint.getMember().getName();
        } else {
            name = batchProperty;
        }
        final List<? extends Pair<String, String>> properties = provider.getInjectables().getProperties();
        final ListIterator<? extends Pair<String, String>> iterator = properties.listIterator(properties.size());
        while (iterator.hasPrevious()) {
            final Pair<String, String> pair = iterator.previous();
            if (name.equals(pair.getName())) {
                return pair.getValue();
            }
        }
        return null;
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
