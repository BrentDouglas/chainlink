package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.partition.Reducer;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;

import javax.batch.api.partition.PartitionReducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerImpl extends PropertyReferenceImpl<PartitionReducer> implements Reducer {

    public ReducerImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void beginPartitionedStep(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, injectionContext, context).beginPartitionedStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforePartitionedStepCompletion(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, injectionContext, context).beforePartitionedStepCompletion();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void rollbackPartitionedStep(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, injectionContext, context).rollbackPartitionedStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterPartitionedStepCompletion(final Executor executor, final ExecutionContext context, final PartitionReducer.PartitionStatus status) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, injectionContext, context).afterPartitionedStepCompletion(status);
        } finally {
            provider.setInjectables(null);
        }
    }
}
