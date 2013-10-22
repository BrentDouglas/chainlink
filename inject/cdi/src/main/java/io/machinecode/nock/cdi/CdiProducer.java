package io.machinecode.nock.cdi;

import io.machinecode.nock.spi.extension.ContextProvider;
import io.machinecode.nock.spi.util.Pair;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
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
public class CdiProducer {

    private ContextProvider provider;

    public CdiProducer() {
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
        final List<? extends Pair<String, String>> properties = provider.getProperties();
        final ListIterator<? extends Pair<String, String>> iterator = properties.listIterator(properties.size());
        while (iterator.hasPrevious()) {
            final Pair<String, String> pair = iterator.previous();
            if (name.equals(pair.getKey())) {
                return pair.getValue();
            }
        }
        return null;
    }

    @Produces
    @Dependent
    public JobContext getJobContext() {
        return provider.getJobContext();
    }

    @Produces
    @Dependent
    public StepContext getStepContext() {
        return provider.getStepContext();
    }
}
