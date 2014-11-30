package io.machinecode.chainlink.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.expression.PropertyContext;
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
        this.references = new THashMap<String, ArtifactReference>();
    }

    @Override
    public Properties getProperties() {
        return properties;
    }

    @Override
    public <T extends ArtifactReference> T getReference(final T that) {
        final T old = (T) references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }
}
