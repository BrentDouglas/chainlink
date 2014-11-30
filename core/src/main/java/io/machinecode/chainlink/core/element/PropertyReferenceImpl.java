package io.machinecode.chainlink.core.element;

import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.PropertyReference;
import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.util.Messages;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class PropertyReferenceImpl<T> implements PropertyReference, Serializable {

    protected final PropertiesImpl properties;
    protected final ArtifactReferenceImpl ref;

    public PropertyReferenceImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
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

    public synchronized T load(final Class<T> clazz, final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final Registry registry = configuration.getRegistry();
        final T artifact = registry.loadArtifact(clazz, this.ref.ref(), context);
        if (artifact != null) {
            return artifact;
        }
        final T that = this.ref.load(clazz, injectionContext, context);
        if (that == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-025004.artifact.null", context, this.ref.ref()));
        }
        registry.storeArtifact(clazz, this.ref.ref(), context, that);
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
