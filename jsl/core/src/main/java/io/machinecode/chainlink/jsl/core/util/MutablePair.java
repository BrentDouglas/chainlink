package io.machinecode.chainlink.jsl.core.util;

import io.machinecode.chainlink.spi.util.Pair;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MutablePair<K,V>  implements Pair<K,V> {

    private K key;
    private V value;

    public MutablePair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getName() {
        return key;
    }

    public void setKey(final K key) {
        this.key = key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public void setValue(final V value) {
        this.value = value;
    }

    public static <K,V> MutablePair<K,V> of(final K key, final V value) {
        return new MutablePair<K, V>(key, value);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof MutablePair)) return false;

        final MutablePair that = (MutablePair) o;

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
