package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Listeners;

import javax.batch.api.listener.JobListener;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobListenersFactory implements ElementFactory<Listeners, ListenersImpl<JobListener>> {

    public static final JobListenersFactory INSTANCE = new JobListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl<JobListener>, JobPropertyContext> JOB_LISTENER_EXECUTION_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl<JobListener>, JobPropertyContext>() {
        @Override
        public ListenerImpl<JobListener> transform(final Listener that, final JobPropertyContext context) {
            return JobListenerFactory.INSTANCE.produceExecution(that, context);
        }
    };

    private static final ExpressionTransformer<ListenerImpl<JobListener>, ListenerImpl<JobListener>, PartitionPropertyContext> JOB_LISTENER_PARTITION_TRANSFORMER = new ExpressionTransformer<ListenerImpl<JobListener>, ListenerImpl<JobListener>, PartitionPropertyContext>() {
        @Override
        public ListenerImpl<JobListener> transform(final ListenerImpl<JobListener> that, final PartitionPropertyContext context) {
            return JobListenerFactory.INSTANCE.producePartitioned(that, context);
        }
    };

    @Override
    public ListenersImpl<JobListener> produceExecution(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl<JobListener>> listeners = that == null
                ? Collections.<ListenerImpl<JobListener>>emptyList()
                : Util.immutableCopy(that.getListeners(), context, JOB_LISTENER_EXECUTION_TRANSFORMER);
        return new ListenersImpl<JobListener>(listeners);
    }

    @Override
    public ListenersImpl<JobListener> producePartitioned(final ListenersImpl<JobListener> that, final PartitionPropertyContext context) {
        return new ListenersImpl<JobListener>(
                Util.immutableCopy(that.getListeners(), context, JOB_LISTENER_PARTITION_TRANSFORMER)
        );
    }
}
