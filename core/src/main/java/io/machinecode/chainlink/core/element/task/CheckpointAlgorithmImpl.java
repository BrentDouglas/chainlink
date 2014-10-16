package io.machinecode.chainlink.core.element.task;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.task.CheckpointAlgorithm;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmImpl extends PropertyReferenceImpl<javax.batch.api.chunk.CheckpointAlgorithm> implements CheckpointAlgorithm {

    public CheckpointAlgorithmImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public int checkpointTimeout(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).checkpointTimeout();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beginCheckpoint(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).beginCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }

    public boolean isReadyToCheckpoint(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.CheckpointAlgorithm.class, configuration, context).isReadyToCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void endCheckpoint(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
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
