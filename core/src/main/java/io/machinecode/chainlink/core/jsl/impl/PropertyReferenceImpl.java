package io.machinecode.chainlink.core.jsl.impl;

import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.PropertyReference;
import io.machinecode.chainlink.spi.registry.Registry;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyReferenceImpl<T> implements PropertyReference, Serializable {
    private static final long serialVersionUID = 1L;

    protected final PropertiesImpl properties;
    protected final ArtifactReference ref;

    public PropertyReferenceImpl(final ArtifactReference ref, final PropertiesImpl properties) {
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

    public synchronized T load(final Class<T> clazz, final Configuration configuration, final ExecutionContext context) throws Exception {
        return load(this.ref, clazz, configuration, context);
    }

    public static synchronized <T> T load(final ArtifactReference ref, final Class<T> clazz, final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final Registry registry = configuration.getRegistry();
        final T artifact = registry.loadArtifact(clazz, ref.ref(), context);
        if (artifact != null) {
            return artifact;
        }
        final T that = ref.load(clazz, injectionContext, context);
        if (that == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-025004.artifact.null", context, ref.ref()));
        }
        registry.storeArtifact(clazz, ref.ref(), context, that);
        return that;
    }

    private transient Injectables _injectables;

    protected synchronized Injectables _injectables(final ExecutionContext context) {
        if (this._injectables == null) {
            this._injectables = new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties()
            );
        }
        return this._injectables;
    }
}
