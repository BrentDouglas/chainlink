package io.machinecode.chainlink.core.util;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.expression.PropertyContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Copy {

    public static <T> List<T> immutableCopy(final List<T> that) {
        return that == null ? Collections.<T>emptyList() : Collections.unmodifiableList(new ArrayList<T>(that));
    }

    public static <T> Set<T> immutableCopy(final Set<T> that) {
        return that == null ? Collections.<T>emptySet() : Collections.unmodifiableSet(new THashSet<T>(that));
    }

    public static <T, U> List<U> immutableCopy(final List<? extends T> that, final Transformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U> List<U> immutableCopy(final List<? extends T> that, final NextTransformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        for (int i = 0, j = 1; i < that.size(); ++i, ++j) {
            final T value = that.get(i);
            final T next = j < that.size() ? that.get(j) : null;

            final U replaced = transformer.transform(value, next);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U, V extends PropertyContext> List<U> immutableCopy(final List<? extends T> that, final V context, final ExpressionTransformer<T, U, V> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value, context);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U, V extends PropertyContext> List<U> immutableCopy(final List<? extends T> that, final V context, final NextExpressionTransformer<T, U, V> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        final int j = that.size() - 1;
        final T next = j < 0 ? null : that.get(j);
        for (int i = 0; i < that.size(); ++i) {
            final T value = that.get(i);
            final U replaced = transformer.transform(value, next, i == j, context);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U> List<U> immutableCopy(final List<? extends T> that, final Properties parameters, final ParametersTransformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        for (final T value : that) {
            final U replaced = transformer.transform(value, parameters);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U> List<U> immutableCopy(final List<? extends T> that, final Properties parameters, final NextParametersTransformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        for (int i = 0, j = 1; i < that.size(); ++i, ++j) {
            final T value = that.get(i);
            final T next = j < that.size() ? that.get(j) : null;

            final U replaced = transformer.transform(value, next, parameters);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public interface Transformer<T, U> {
        U transform(final T that);
    }

    public interface NextTransformer<T, U> {
        U transform(final T that, final T next);
    }

    public interface ExpressionTransformer<T, U, V extends PropertyContext> {
        U transform(final T that, final V context);
    }

    public interface NextExpressionTransformer<T, U, V extends PropertyContext> {
        U transform(final T that, final T next, final boolean last, final V context);
    }

    public interface ParametersTransformer<T, U> {
        U transform(final T that, final Properties parameters);
    }

    public interface NextParametersTransformer<T, U> {
        U transform(final T that, final T next, final Properties parameters);
    }
}
