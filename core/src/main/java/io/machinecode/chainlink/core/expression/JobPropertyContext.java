package io.machinecode.chainlink.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.jsl.Property;

import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobPropertyContext implements PropertyContext {

    private final Properties properties;
    private final Properties parameters;
    private final TMap<String,ArtifactReference> references;

    public JobPropertyContext(final Properties parameters) {
        this.properties = new Properties();
        this.parameters = parameters;
        this.references = new THashMap<>();
    }

    public void addProperty(final Property property) {
        this.properties.put(property.getName(), property.getValue());
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public ArtifactReference getReference(final ArtifactReference that) {
        final ArtifactReference old = references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }

    public Properties getParameters() {
        return this.parameters;
    }
}
