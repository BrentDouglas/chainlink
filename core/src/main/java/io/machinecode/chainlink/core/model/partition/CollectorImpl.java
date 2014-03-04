package io.machinecode.chainlink.core.model.partition;

import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.core.model.PropertyReferenceImpl;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.util.Messages;

import javax.batch.api.partition.PartitionCollector;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorImpl extends PropertyReferenceImpl<PartitionCollector> implements Collector {

    public CollectorImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public Serializable collectPartitionData(final Executor executor, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(PartitionCollector.class, injectionContext, context).collectPartitionData();
        } finally {
            provider.setInjectables(null);
        }
    }
}
