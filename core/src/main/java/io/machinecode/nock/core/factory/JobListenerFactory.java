package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.descriptor.ListenerImpl;
import io.machinecode.nock.core.descriptor.PropertiesImpl;
import io.machinecode.nock.core.expression.Expression;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.core.work.ListenerWork;
import io.machinecode.nock.spi.element.Listener;

import javax.batch.api.listener.JobListener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobListenerFactory implements ElementFactory<Listener, ListenerImpl, ListenerWork<JobListener>> {

    public static final JobListenerFactory INSTANCE = new JobListenerFactory();

    @Override
    public ListenerImpl produceDescriptor(final Listener that, final JobPropertyContext context) {
        final String ref = Expression.resolveDescriptorProperty(that.getRef(), context);
        final PropertiesImpl properties = PropertiesFactory.INSTANCE.produceDescriptor(that.getProperties(), context);
        return new ListenerImpl(
            ref,
            properties
        );
    }

    @Override
    public ListenerWork<JobListener> produceExecution(final ListenerImpl that, final JobParameterContext context) {
        final String ref = Expression.resolveExecutionProperty(that.getRef(), context);
        return new ListenerWork<JobListener>(new ResolvableReference<JobListener>(
                ref,
                JobListener.class
        ));
    }

    @Override
    public ListenerWork<JobListener> producePartitioned(final ListenerWork<JobListener> that, final PartitionPropertyContext context) {
        final String ref = Expression.resolvePartitionProperty(that.getRef(), context);
        return new ListenerWork<JobListener>(new ResolvableReference<JobListener>(
                ref,
                JobListener.class
        ));
    }
}
