package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.factory.ElementFactory;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.inject.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.AnalyserImpl;
import io.machinecode.chainlink.spi.expression.PropertyContext;
import io.machinecode.chainlink.spi.jsl.partition.Analyser;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class AnalyserFactory implements ElementFactory<Analyser, AnalyserImpl> {

    public static final AnalyserFactory INSTANCE = new AnalyserFactory();

    @Override
    public AnalyserImpl produceExecution(final Analyser that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new AnalyserImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public AnalyserImpl producePartitioned(final AnalyserImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new AnalyserImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
