package io.machinecode.nock.core.expression;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.util.MutablePair;
import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.inject.ArtifactReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobPropertyContextImpl implements JobPropertyContext {

    private final List<MutablePair<String,String>> properties;
    private final List<MutablePair<String,String>> parameters;
    private final TMap<String,ArtifactReference> references;

    public JobPropertyContextImpl(final java.util.Properties parameters) {
        this.properties = new ArrayList<MutablePair<String, String>>();
        this.parameters = new ArrayList<MutablePair<String, String>>();
        this.references = new THashMap<String, ArtifactReference>();
        if (parameters == null) {
            return;
        }
        for (final Entry<Object, Object> entry : parameters.entrySet()) {
            final Object key = entry.getKey();
            final Object value = entry.getValue();
            this.parameters.add(MutablePair.of(
                    key instanceof String ? (String)key : key.toString(),
                    value instanceof String ? (String)value : value.toString()
            ));
        }
    }

    public void addProperty(final Property property) {
        this.properties.add(MutablePair.of(property.getName(), property.getValue()));
    }

    @Override
    public List<MutablePair<String, String>> getProperties() {
        return Collections.unmodifiableList(this.properties);
    }

    @Override
    public <T extends ArtifactReference> T getReference(final T that) {
        final T old = (T) references.putIfAbsent(that.ref(), that);
        return old == null ? that : old;
    }

    @Override
    public List<MutablePair<String, String>> getParameters() {
        return this.parameters;
    }
}
