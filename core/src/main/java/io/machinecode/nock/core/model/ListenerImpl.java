package io.machinecode.nock.core.model;

import io.machinecode.nock.core.loader.ArtifactReference;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.transport.Transport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl implements Listener, PropertyReference {

    protected final PropertiesImpl properties;
    protected final ArtifactReference ref;

    public ListenerImpl(final ArtifactReference ref, final PropertiesImpl properties) {
        this.ref = ref;
        this.properties = properties;
    }

    @Override
    public String getRef() {
        return this.ref.ref();
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    public <T> T load(final Class<T> clazz, final Transport transport, final Context context) throws Exception {
        return this.ref.load(clazz, transport, context, this);
    }
}
