package io.machinecode.nock.jsl.util;

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
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public static <K,V> ImmutablePair<K,V> of(final K key, final V value) {
        return new ImmutablePair<K, V>(key, value);
    }
}
