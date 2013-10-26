package io.machinecode.nock.core.loader;

import io.machinecode.nock.core.local.InjectablesImpl;
import io.machinecode.nock.core.util.ResolvableClass;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.transport.Transport;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TypedArtifactReference<T> {

    private final String ref;
    private final ResolvableClass<T> clazz;
    private transient Class<T> _clazz;

    public TypedArtifactReference(final String ref, final Class<T> clazz) {
        this.ref = ref;
        this.clazz = new ResolvableClass<T>(clazz);
    }

    public T load(final Transport transport, final Context context, final Element element) throws Exception {
        final InjectionContext injectionContext = transport.createInjectionContext(context);
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    context.getJob().properties(element))
            );
            final ClassLoader classLoader = injectionContext.getClassLoader();
            final T that =  injectionContext.getArtifactLoader().load(this.ref, this.type(classLoader), classLoader);
            injectionContext.getInjector().inject(that);
            return that;
        } finally {
            provider.setInjectables(null);
        }
    }

    public String ref() {
        return this.ref;
    }

    public Class<T> type(final ClassLoader classLoader) {
        if (this._clazz == null) {
            try {
                this._clazz = this.clazz.resolve(classLoader);
            } catch (final ClassNotFoundException e) {
                throw new BatchRuntimeException(e);
            }
        }
        return this._clazz;
    }
}
