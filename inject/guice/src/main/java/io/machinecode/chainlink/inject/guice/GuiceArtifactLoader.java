package io.machinecode.chainlink.inject.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.inject.core.DefaultInjector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class GuiceArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(GuiceArtifactLoader.class);

    private final InjectablesProvider provider;
    final Injector injector;

    public GuiceArtifactLoader(final BindingProvider binding) {
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
        final List<BindingProvider.Binding> bindings = binding.getBindings();
        final Set<BatchProperty> props = new THashSet<BatchProperty>();
        this.injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                for (final BindingProvider.Binding that : bindings) {
                    final Class clazz = that.getType();
                    bind(that.getSatisfies()).annotatedWith(Names.named(that.getName())).to(clazz);
                    for (final Field field : clazz.getDeclaredFields()) {
                        final BatchProperty batchProperty = field.getAnnotation(BatchProperty.class);
                        if (batchProperty == null || !String.class.equals(field.getType()) || props.contains(batchProperty)) {
                            continue;
                        }
                        props.add(batchProperty);
                        bind(String.class).annotatedWith(batchProperty).toProvider(new StringProvider(batchProperty));
                    }
                }
                bind(JobContext.class).toProvider(new Provider<JobContext>() {
                    @Override
                    public JobContext get() {
                        return provider.getInjectables().getJobContext();
                    }
                });
                bind(StepContext.class).toProvider(new Provider<StepContext>() {
                    @Override
                    public StepContext get() {
                        return provider.getInjectables().getStepContext();
                    }
                });
                bindListener(Matchers.any(), new TypeListener() {
                    @Override
                    public <I> void hear(final TypeLiteral<I> type, final TypeEncounter<I> encounter) {
                        for (final Field field : type.getRawType().getDeclaredFields()) {
                            final BatchProperty batchProperty = field.getAnnotation(BatchProperty.class);
                            if (batchProperty != null && String.class.equals(field.getType())) {
                                encounter.register(new StringInjector<I>(field, batchProperty));
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws ArtifactOfWrongTypeException {
        try {
            return injector.getInstance(Key.get(as, Names.named(id)));
        } catch (final ProvisionException e) {
            log.tracef(e, Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
    }

    public class StringProvider implements Provider<String> {

        private final BatchProperty batchProperty;

        public StringProvider(final BatchProperty batchProperty) {
            this.batchProperty = batchProperty;
        }

        @Override
        public String get() {
            //TODO Skip field part as this can be for multiple bindings. Field needs to get set by the type listener
            final String proprety = DefaultInjector.property(batchProperty.name(), batchProperty.name(), provider.getInjectables().getProperties());
            return proprety == null ? "" : proprety;
        }
    }

    public class StringInjector<T> implements MembersInjector<T> {

        private final Field field;
        private final BatchProperty batchProperty;

        public StringInjector(final Field field, final BatchProperty batchProperty) {
            this.field = field;
            this.batchProperty = batchProperty;
        }

        @Override
        public void injectMembers(final T instance) {
            final String proprety = DefaultInjector.property(batchProperty.name(), field.getName(), provider.getInjectables().getProperties());
            try {
                DefaultInjector.set(field, instance, proprety);
            } catch (final IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
