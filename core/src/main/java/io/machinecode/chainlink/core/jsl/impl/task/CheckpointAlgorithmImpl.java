package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertyReferenceImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.task.CheckpointAlgorithm;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CheckpointAlgorithmImpl extends PropertyReferenceImpl<javax.batch.api.chunk.CheckpointAlgorithm> implements CheckpointAlgorithm {
    private static final long serialVersionUID = 1L;

    public CheckpointAlgorithmImpl(final ArtifactReference ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public int checkpointTimeout(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).checkpointTimeout();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beginCheckpoint(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).beginCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }

    public boolean isReadyToCheckpoint(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).isReadyToCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void endCheckpoint(final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).endCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }
}
