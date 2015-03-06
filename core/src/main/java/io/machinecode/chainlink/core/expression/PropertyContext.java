package io.machinecode.chainlink.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.inject.ArtifactReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class PropertyContext {

    private final TMap<String,ArtifactReference> references = new THashMap<>();
    final PropertyResolver properties;

    protected PropertyContext(final PropertyResolver properties) {
        this.properties = properties;
    }

    //TODO This doesn't really belong in the Expression package
    public ArtifactReference getReference(final ArtifactReference that) {
        final ArtifactReference old = references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }
}
