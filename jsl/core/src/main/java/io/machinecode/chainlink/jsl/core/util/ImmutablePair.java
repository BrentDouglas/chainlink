package io.machinecode.chainlink.jsl.core.util;

import io.machinecode.chainlink.spi.util.Pair;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ImmutablePair<K,V> implements Pair<K,V> {

    private final K key;
    private final V value;

    public ImmutablePair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getName() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public static <K,V> ImmutablePair<K,V> of(final K key, final V value) {
        return new ImmutablePair<K, V>(key, value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutablePair)) return false;

        final ImmutablePair that = (ImmutablePair) o;

        if (!key.equals(that.key)) return false;
        if (!value.equals(that.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
