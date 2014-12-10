package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import java.io.Serializable;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ItemWriterImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemWriter> implements ItemWriter {
    private static final long serialVersionUID = 1L;

    public ItemWriterImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void open(final RuntimeConfiguration configuration, final ExecutionContext context, final Serializable checkpoint) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemWriter.class, configuration, context).open(checkpoint);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void close(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemWriter.class, configuration, context).close();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void writeItems(final RuntimeConfiguration configuration, final ExecutionContext context, final List<Object> items) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemWriter.class, configuration, context).writeItems(items);
        } finally {
            provider.setInjectables(null);
        }
    }

    public Serializable checkpointInfo(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.ItemWriter.class, configuration, context).checkpointInfo();
        } finally {
            provider.setInjectables(null);
        }
    }
}
