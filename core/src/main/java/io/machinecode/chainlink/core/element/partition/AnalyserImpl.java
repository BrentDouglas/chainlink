package io.machinecode.chainlink.core.element.partition;

import io.machinecode.chainlink.core.element.PropertiesImpl;
import io.machinecode.chainlink.core.element.PropertyReferenceImpl;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.element.partition.Analyser;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import javax.batch.api.partition.PartitionAnalyzer;
import javax.batch.runtime.BatchStatus;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AnalyserImpl extends PropertyReferenceImpl<PartitionAnalyzer> implements Analyser {

    public AnalyserImpl(final ArtifactReferenceImpl ref, final PropertiesImpl properties) {
        super(ref, properties);
    }

    public void analyzeCollectorData(final RuntimeConfiguration configuration, final ExecutionContext context, final Serializable data) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionAnalyzer.class, configuration, context).analyzeCollectorData(data);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void analyzeStatus(final RuntimeConfiguration configuration, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionAnalyzer.class, configuration, context).analyzeStatus(batchStatus, exitStatus);
        } finally {
            provider.setInjectables(null);
        }
    }
}
