package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import java.io.Serializable;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemWriter> implements ItemWriter {

    public ItemWriterImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void open(final Executor executor, final ExecutionContext context, final Serializable checkpoint) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemWriter.class, injectionContext, context).open(checkpoint);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void close(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemWriter.class, injectionContext, context).close();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void writeItems(final Executor executor, final ExecutionContext context, final List<Object> items) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.ItemWriter.class, injectionContext, context).writeItems(items);
        } finally {
            provider.setInjectables(null);
        }
    }

    public Serializable checkpointInfo(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.ItemWriter.class, injectionContext, context).checkpointInfo();
        } finally {
            provider.setInjectables(null);
        }
    }
}
