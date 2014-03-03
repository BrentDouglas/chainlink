package io.machinecode.nock.core.model;

import io.machinecode.nock.core.impl.InjectablesImpl;
import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.Injectables;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyReferenceImpl<T> implements PropertyReference {

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
        final T that = this.ref.load(clazz, injectionContext);
        if (that == null) {
            throw new IllegalStateException(Messages.format("NOCK-025004.artifact.null", context, this.ref.ref()));
        }
        this._cached = that;
        return that;
    }

    private transient Injectables _injectables;

    protected Injectables _injectables(final ExecutionContext context) {
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
