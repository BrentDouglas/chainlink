package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.descriptor.ListenerImpl;
import io.machinecode.nock.core.descriptor.ListenersImpl;
import io.machinecode.nock.core.expression.JobParameterContext;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.work.ListenerWork;
import io.machinecode.nock.core.work.ListenersWork;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;

import javax.batch.api.listener.JobListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobListenersFactory implements ElementFactory<Listeners, ListenersImpl, ListenersWork<JobListener>> {

    public static final JobListenersFactory INSTANCE = new JobListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext> LISTENER_BUILD_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
            return JobListenerFactory.INSTANCE.produceDescriptor(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerImpl, ListenerWork<JobListener>, JobParameterContext> JOB_LISTENER_EXECUTION_TRANSFORMER = new ExpressionTransformer<ListenerImpl, ListenerWork<JobListener>, JobParameterContext>() {
        @Override
        public ListenerWork<JobListener> transform(final ListenerImpl that, final JobParameterContext context) {
            return JobListenerFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerWork<JobListener>, ListenerWork<JobListener>, PartitionPropertyContext> JOB_LISTENER_PARTITION_TRANSFORMER = new ExpressionTransformer<ListenerWork<JobListener>, ListenerWork<JobListener>, PartitionPropertyContext>() {
        @Override
        public ListenerWork<JobListener> transform(final ListenerWork<JobListener> that, final PartitionPropertyContext context) {
            return JobListenerFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ListenersImpl produceDescriptor(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl> listeners = that == null
                ? Collections.<ListenerImpl>emptyList()
                : Util.immutableCopy(that.getListeners(), context, LISTENER_BUILD_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }

    @Override
    public ListenersWork<JobListener> produceExecution(final ListenersImpl that, final JobParameterContext context) {
        return new ListenersWork<JobListener>(
                Util.immutableCopy(that.getListeners(), context, JOB_LISTENER_EXECUTION_TRANSFORMER)
        );
    }

    @Override
    public ListenersWork<JobListener> producePartitioned(final ListenersWork<JobListener> that, final PartitionPropertyContext context) {
        return new ListenersWork<JobListener>(
                Util.immutableCopy(that.getListeners(), context, JOB_LISTENER_PARTITION_TRANSFORMER)
        );
    }
}
