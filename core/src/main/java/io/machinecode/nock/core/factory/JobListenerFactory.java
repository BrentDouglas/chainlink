package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.loader.UntypedArtifactReference;
import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.factory.ElementFactory;
import io.machinecode.nock.spi.factory.JobPropertyContext;
import io.machinecode.nock.spi.factory.PropertyContext;

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
                context.getReference(new UntypedArtifactReference(ref)),
                properties
        );
    }

    @Override
    public ListenerImpl producePartitioned(final ListenerImpl that, final PropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.producePartitioned(that.getProperties(), context);
        return new ListenerImpl(
                context.getReference(new UntypedArtifactReference(ref)),
                properties
        );
    }
}
