package io.machinecode.chainlink.core.element;

import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.PropertyReference;
import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.util.Messages;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl<T> implements PropertyReference, Serializable {

    protected final PropertiesImpl properties;
    protected final ArtifactReferenceImpl ref;
    protected transient T _cached;

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

    public synchronized T load(final Class<T> clazz, final InjectionContext injectionContext, final ExecutionContext context) throws Exception {
        if (this._cached != null) {
            return this._cached;
        }
        final T that = this.ref.load(clazz, injectionContext, context);
        if (that == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-025004.artifact.null", context, this.ref.ref()));
        }
        this._cached = that;
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
