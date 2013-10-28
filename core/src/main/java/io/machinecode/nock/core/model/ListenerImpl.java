package io.machinecode.nock.core.model;

import io.machinecode.nock.core.loader.UntypedArtifactReference;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.transport.Transport;
import org.jboss.logging.Logger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl implements Listener, PropertyReference {

    private static final Logger log = Logger.getLogger(ListenerImpl.class);

    protected final PropertiesImpl properties;
    protected final UntypedArtifactReference ref;

    public ListenerImpl(final UntypedArtifactReference ref, final PropertiesImpl properties) {
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
