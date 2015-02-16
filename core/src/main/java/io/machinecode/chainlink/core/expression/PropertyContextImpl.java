package io.machinecode.chainlink.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.inject.ArtifactReference;

import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class PropertyContextImpl implements PropertyContext {

    private final Properties properties;
    private final TMap<String,ArtifactReference> references;

    public PropertyContextImpl() {
        this(new Properties());
    }

    public PropertyContextImpl(final Properties properties) {
        this.properties = properties;
        this.references = new THashMap<>();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public ArtifactReference getReference(final ArtifactReference that) {
        final ArtifactReference old = references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }
}
