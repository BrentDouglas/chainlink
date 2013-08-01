package io.machinecode.nock.jsl.impl;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public static <T> List<T> immutableCopy(final List<? extends T> that, final Transformer<T> transformer) {
        if (that == null) {
            return Collections.<T>emptyList();
        }
        final List<T> list = new ArrayList<T>(that.size());
        for (final T value : that) {
            final T replaced = transformer.transform(value);
            if (replaced != null) {
                list.add(replaced);
            }
        }
        return Collections.unmodifiableList(list);
    }

    public static <T> List<T> immutableCopy(final List<? extends T> that, final NextTransformer<T> transformer) {
        if (that == null) {
            return Collections.<T>emptyList();
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

    public interface Transformer<T> {
        T transform(final T that);
    }

    public interface NextTransformer<T> {
        T transform(final T that, final T next);
    }
}
