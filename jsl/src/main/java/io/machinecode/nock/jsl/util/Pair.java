package io.machinecode.nock.jsl.util;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Pair<K,V> {

    private final K key;
    private final V value;

    public Pair(final K key, final V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    public static <K,V> Pair<K,V> of(final K key, final V value) {
        return new Pair<K, V>(key, value);
    }
}
