package io.machinecode.chainlink.core.loader;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ArtifactReferenceImpl implements ArtifactReference {

    private final String ref;

    public ArtifactReferenceImpl(final String ref) {
        this.ref = ref;
    }

    public <T> T load(final Class<T> clazz, final InjectionContext injectionContext, final ExecutionContext context) throws Exception {
        final ClassLoader classLoader = injectionContext.getClassLoader();
        final T that = injectionContext.getArtifactLoader().load(this.ref, clazz, classLoader);
        if (that != null) {
            injectionContext.getInjector().inject(that);
        }
        return that;
    }

    @Override
    public String ref() {
        return this.ref;
    }
}
