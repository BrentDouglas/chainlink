package io.machinecode.nock.core.util;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.core.expression.PropertyContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Util {

    public static <T> List<T> immutableCopy(final List<T> that) {
        return that == null ? Collections.<T>emptyList() : Collections.unmodifiableList(new ArrayList<T>(that));
    }

    public static <T> Set<T> immutableCopy(final Set<T> that) {
        return that == null ? Collections.<T>emptySet() : Collections.unmodifiableSet(new THashSet<T>(that));
    }

    public static <T, U extends T> List<U> immutableCopy(final List<? extends T> that, final Transformer<T, U> transformer) {
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

    public static <T, U extends T> List<T> immutableCopy(final List<? extends T> that, final NextTransformer<T, U> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<T> list = new ArrayList<T>(that.size());
        for (int i = 0, j = 1; i < that.size(); ++i, ++j) {
            final T value = that.get(i);
            final T next = j < that.size() ? that.get(j) : null;

            final T replaced = transformer.transform(value, next);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U extends T, V extends PropertyContext> List<U> immutableCopy(final List<? extends T> that, final V context, final ExpressionTransformer<T, U, V> transformer) {
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

    public static <T, U extends T, V extends PropertyContext> List<U> immutableCopy(final List<? extends T> that, final V context, final NextExpressionTransformer<T, U, V> transformer) {
        if (that == null) {
            return Collections.emptyList();
        }
        final List<U> list = new ArrayList<U>(that.size());
        for (int i = 0, j = 1; i < that.size(); ++i, ++j) {
            final T value = that.get(i);
            final T next = j < that.size() ? that.get(j) : null;

            final U replaced = transformer.transform(value, next, context);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T, U extends T> List<U> immutableCopy(final List<? extends T> that, final Properties parameters, final ParametersTransformer<T, U> transformer) {
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

    public static <T, U extends T> List<U> immutableCopy(final List<? extends T> that, final Properties parameters, final NextParametersTransformer<T, U> transformer) {
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

    public interface Transformer<T, U extends T> {
        U transform(final T that);
    }

    public interface NextTransformer<T, U extends T> {
        U transform(final T that, final T next);
    }

    public interface ExpressionTransformer<T, U extends T, V extends PropertyContext> {
        U transform(final T that, final V context);
    }

    public interface NextExpressionTransformer<T, U, V extends PropertyContext> {
        U transform(final T that, final T next, final V context);
    }

    public interface ParametersTransformer<T, U extends T> {
        U transform(final T that, final Properties parameters);
    }

    public interface NextParametersTransformer<T, U> {
        U transform(final T that, final T next, final Properties parameters);
    }
}
