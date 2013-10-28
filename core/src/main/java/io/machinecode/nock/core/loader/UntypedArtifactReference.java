package io.machinecode.nock.core.loader;

import io.machinecode.nock.core.local.InjectablesImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.inject.ArtifactReference;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class UntypedArtifactReference implements ArtifactReference {

    private final String ref;

    public UntypedArtifactReference(final String ref) {
        this.ref = ref;
    }

    public <T> T load(final Class<T> clazz, final Transport transport, final Context context, final Element element) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    context.getJob().properties(element))
            );
            final ClassLoader classLoader = injectionContext.getClassLoader();
            final T that = injectionContext.getArtifactLoader().load(this.ref, clazz, classLoader);
            if (that != null) {
                injectionContext.getInjector().inject(that);
            }
            return that;
        } finally {
            provider.setInjectables(null);
        }
    }

    @Override
    public String ref() {
        return this.ref;
    }
}
