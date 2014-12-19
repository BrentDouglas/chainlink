package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ArtifactReferenceImpl implements ArtifactReference, Serializable {
    private static final long serialVersionUID = 1L;

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
