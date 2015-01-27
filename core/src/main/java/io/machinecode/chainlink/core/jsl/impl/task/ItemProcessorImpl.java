package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertyReferenceImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.task.ItemProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemProcessorImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemProcessor> implements ItemProcessor {
    private static final long serialVersionUID = 1L;

    public ItemProcessorImpl(final ArtifactReference ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public Object processItem(final Configuration configuration, final ExecutionContext context, final Object item) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.ItemProcessor.class, configuration, context).processItem(item);
        } finally {
            provider.setInjectables(null);
        }
    }
}
