package io.machinecode.chainlink.core.factory;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.loader.ArtifactReferenceImpl;
import io.machinecode.chainlink.core.model.ListenerImpl;
import io.machinecode.chainlink.core.model.PropertiesImpl;
import io.machinecode.chainlink.spi.element.Listener;
import io.machinecode.chainlink.spi.factory.ElementFactory;
import io.machinecode.chainlink.spi.factory.JobPropertyContext;
import io.machinecode.chainlink.spi.factory.PropertyContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobListenerFactory implements ElementFactory<Listener, ListenerImpl> {

    public static final JobListenerFactory INSTANCE = new JobListenerFactory();

    @Override
    public ListenerImpl produceExecution(final Listener that, final JobPropertyContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceExecution(that.getProperties(), context);
        return new ListenerImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }

    @Override
    public ListenerImpl producePartitioned(final ListenerImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ListenerImpl(
                context.getReference(new ArtifactReferenceImpl(ref)),
                properties
        );
    }
}
