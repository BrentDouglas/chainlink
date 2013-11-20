package io.machinecode.nock.core.loader;

import io.machinecode.nock.core.impl.InjectablesImpl;
import io.machinecode.nock.core.util.ResolvableClass;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.inject.ArtifactReference;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;
import io.machinecode.nock.spi.execution.Executor;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TypedArtifactReference<T> implements ArtifactReference {

    private static final Logger log = Logger.getLogger(TypedArtifactReference.class);

    private final String ref;
    private final ResolvableClass<T> clazz;
    private transient Class<T> _clazz;

    public TypedArtifactReference(final String ref, final Class<T> clazz) {
        this.ref = ref;
        this.clazz = new ResolvableClass<T>(clazz);
    }

    public T load(final Executor executor, final ExecutionContext context, final Element element) throws Exception {
        final InjectionContext injectionContext = executor.createInjectionContext(context);
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    context.getJob().getProperties(element))
            );
            final ClassLoader classLoader = injectionContext.getClassLoader();
            final T that =  injectionContext.getArtifactLoader().load(this.ref, this.type(classLoader), classLoader);
            if (that != null) {
                injectionContext.getInjector().inject(that);
            }
            return that;
        } finally {
            provider.setInjectables(null);
        }
    }

    @Override
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
