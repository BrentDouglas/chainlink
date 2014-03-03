package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.impl.InjectablesImpl;
import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.task.CheckpointAlgorithm;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CheckpointAlgorithmImpl extends PropertyReferenceImpl<javax.batch.api.chunk.CheckpointAlgorithm> implements CheckpointAlgorithm {

    public CheckpointAlgorithmImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public int checkpointTimeout(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.CheckpointAlgorithm.class, injectionContext, context).checkpointTimeout();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beginCheckpoint(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.CheckpointAlgorithm.class, injectionContext, context).beginCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }

    public boolean isReadyToCheckpoint(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(javax.batch.api.chunk.CheckpointAlgorithm.class, injectionContext, context).isReadyToCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void endCheckpoint(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(javax.batch.api.chunk.CheckpointAlgorithm.class, injectionContext, context).endCheckpoint();
        } finally {
            provider.setInjectables(null);
        }
    }
}
