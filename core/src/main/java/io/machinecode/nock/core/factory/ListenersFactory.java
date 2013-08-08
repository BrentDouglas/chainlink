package io.machinecode.nock.core.factory;

import io.machinecode.nock.core.expression.JobPropertyContext;
import io.machinecode.nock.core.model.ListenerImpl;
import io.machinecode.nock.core.model.ListenersImpl;
import io.machinecode.nock.core.util.Util;
import io.machinecode.nock.core.util.Util.ExpressionTransformer;
import io.machinecode.nock.core.util.Util.ParametersTransformer;
import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Listeners;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenersFactory implements ElementFactory<Listeners, ListenersImpl> {

    public static final ListenersFactory INSTANCE = new ListenersFactory();

    private static final ExpressionTransformer<Listener, ListenerImpl> LISTENER_BUILD_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
            return ListenerFactory.INSTANCE.produceBuildTime(that, context);
        }
    };

    private static final ParametersTransformer<Listener, ListenerImpl> LISTENER_START_TRANSFORMER = new ParametersTransformer<Listener, ListenerImpl>() {
        @Override
        public ListenerImpl transform(final Listener that, final Properties parameters) {
            return ListenerFactory.INSTANCE.produceStartTime(that, parameters);
        }
    };

    private static final ExpressionTransformer<Listener, ListenerImpl> LISTENER_RUN_TRANSFORMER = new ExpressionTransformer<Listener, ListenerImpl>() {
        @Override
        public ListenerImpl transform(final Listener that, final JobPropertyContext context) {
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
    public ListenersImpl produceStartTime(final Listeners that, final Properties parameters) {
        final List<ListenerImpl> listeners = Util.immutableCopy(that.getListeners(), parameters, LISTENER_START_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }

    @Override
    public ListenersImpl producePartitionTime(final Listeners that, final JobPropertyContext context) {
        final List<ListenerImpl> listeners = Util.immutableCopy(that.getListeners(), context, LISTENER_RUN_TRANSFORMER);
        return new ListenersImpl(
                listeners
        );
    }
}
