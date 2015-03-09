package io.machinecode.chainlink.core.util;

import io.machinecode.chainlink.core.expression.PropertyContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Copy {

    private static final Transformer<?,?> NOOP = new Transformer<Object, Object>() {
        @Override
        public Object transform(final Object that) {
            return that;
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Transformer<T,T> noop() {
        return (Transformer<T,T>)NOOP;
    }

    public static <T, U, V extends PropertyContext> List<U> immutableCopy(final List<? extends T> that, final V context, final ExpressionTransformer<T, U, V> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value, context);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U> List<U> copy(final List<? extends T> that, final Transformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return list;
    }

    public interface Transformer<T, U> {
        U transform(final T that);
    }

    public interface ExpressionTransformer<T, U, V extends PropertyContext> {
        U transform(final T that, final V context);
    }
}
