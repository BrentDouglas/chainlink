package io.machinecode.chainlink.inject.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
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
import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GuiceArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(GuiceArtifactLoader.class);

    private final InjectablesProvider provider;
    final com.google.inject.Injector injector;

    public GuiceArtifactLoader(final BindingProvider binding) {
        this.provider = ArtifactLoaderImpl.loadProvider();
        final List<BindingProvider.Binding> bindings = binding.getBindings();
        final Set<BatchProperty> props = new THashSet<>();
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
            final String property = Injector.property(batchProperty.name(), batchProperty.name(), provider.getInjectables().getProperties());
            return property == null ? "" : property;
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
            final String proprety = Injector.property(batchProperty.name(), field.getName(), provider.getInjectables().getProperties());
            try {
                Injector.set(field, instance, proprety);
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
