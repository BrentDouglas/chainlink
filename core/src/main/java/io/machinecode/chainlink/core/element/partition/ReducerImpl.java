package io.machinecode.chainlink.core.element.partition;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.partition.Reducer;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import javax.batch.api.partition.PartitionReducer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ReducerImpl extends PropertyReferenceImpl<PartitionReducer> implements Reducer {

    public ReducerImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void beginPartitionedStep(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).beginPartitionedStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void beforePartitionedStepCompletion(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).beforePartitionedStepCompletion();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void rollbackPartitionedStep(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).rollbackPartitionedStep();
        } finally {
            provider.setInjectables(null);
        }
    }

    public void afterPartitionedStepCompletion(final RuntimeConfiguration configuration, final ExecutionContext context, final PartitionReducer.PartitionStatus status) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionReducer.class, configuration, context).afterPartitionedStepCompletion(status);
        } finally {
            provider.setInjectables(null);
        }
    }
}
