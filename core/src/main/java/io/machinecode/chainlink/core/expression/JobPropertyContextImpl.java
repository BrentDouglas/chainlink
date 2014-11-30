package io.machinecode.chainlink.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.expression.JobPropertyContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;

import java.util.Properties;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class JobPropertyContextImpl implements JobPropertyContext {

    private final Properties properties;
    private final Properties parameters;
    private final TMap<String,ArtifactReference> references;

    public JobPropertyContextImpl(final Properties parameters) {
        this.properties = new Properties();
        this.parameters = parameters;
        this.references = new THashMap<String, ArtifactReference>();
    }

    public void addProperty(final Property property) {
        this.properties.put(property.getName(), property.getValue());
    }

    @Override
    public Properties getProperties() {
        return this.properties;
    }

    @Override
    public <T extends ArtifactReference> T getReference(final T that) {
        final T old = (T) references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }

    @Override
    public Properties getParameters() {
        return this.parameters;
    }
}
