package io.machinecode.nock.core.model.partition;

import io.machinecode.nock.core.loader.ArtifactReferenceImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.element.partition.Analyser;
import io.machinecode.nock.spi.execution.Executor;
import io.machinecode.nock.spi.inject.InjectablesProvider;
import io.machinecode.nock.spi.inject.InjectionContext;

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

    public void analyzeCollectorData(final Executor executor, final ExecutionContext context, final Serializable data) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionAnalyzer.class, injectionContext, context).analyzeCollectorData(data);
        } finally {
            provider.setInjectables(null);
        }
    }

    public void analyzeStatus(final Executor executor, final ExecutionContext context, final BatchStatus batchStatus, final String exitStatus) throws Exception {
        final InjectionContext injectionContext = executor.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            provider.setInjectables(_injectables(context));
            load(PartitionAnalyzer.class, injectionContext, context).analyzeStatus(batchStatus, exitStatus);
        } finally {
            provider.setInjectables(null);
        }
    }
}
