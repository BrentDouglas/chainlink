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

    public interface ExpressionTransformer<T, U, V extends PropertyContext> {
        U transform(final T that, final V context);
    }
}
