package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.expression.PartitionPropertyContext;
import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersFactory implements ElementFactory<Listeners, ListenersImpl> {

    public static final ListenersFactory INSTANCE = new ListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext> LISTENER_BUILD_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl, JobPropertyContext>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
            return ListenerFactory.INSTANCE.produceBuildTime(that, context);
        }
    };

    private static final ExpressionTransformer<Listener, ListenerImpl, PartitionPropertyContext> LISTENER_RUN_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl, PartitionPropertyContext>() {
        @Override
        public ListenerImpl transform(final Listener that, final PartitionPropertyContext context) {
            return ListenerFactory.INSTANCE.producePartitionTime(that, context);
        }
    };

    @Override
    public ListenersImpl produceBuildTime(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl> listeners = that == null
                ? Collections.<ListenerImpl>emptyList()
                : Util.immutableCopy(that.getListeners(), context, LISTENER_BUILD_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }

    @Override
    public ListenersImpl producePartitionTime(final Listeners that, final PartitionPropertyContext context) {
        final List<ListenerImpl> listeners = Util.immutableCopy(that.getListeners(), context, LISTENER_RUN_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }
}
