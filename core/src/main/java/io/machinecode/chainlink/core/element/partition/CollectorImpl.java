package io.machinecode.chainlink.core.element.partition;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import javax.batch.api.partition.PartitionCollector;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CollectorImpl extends PropertyReferenceImpl<PartitionCollector> implements Collector {

    public CollectorImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public Serializable collectPartitionData(final RuntimeConfiguration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            return load(PartitionCollector.class, configuration, context).collectPartitionData();
        } finally {
            provider.setInjectables(null);
        }
    }
}
