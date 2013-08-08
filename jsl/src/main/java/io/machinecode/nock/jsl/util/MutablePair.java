package io.machinecode.nock.jsl.util;

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
    public K getKey() {
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
}
