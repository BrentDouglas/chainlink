package io.machinecode.chainlink.repository.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class CacheMap<K,V> implements Map<K,V> {

    @Override
    public int size() { throw new IllegalStateException(); }

    @Override
    public boolean isEmpty() { throw new IllegalStateException(); }

    @Override
    public boolean containsKey(final Object key) { throw new IllegalStateException(); }

    @Override
    public boolean containsValue(final Object value) { throw new IllegalStateException(); }

    @Override
    public void clear() { throw new IllegalStateException(); }

    @Override
    public Set<K> keySet() { throw new IllegalStateException(); }

    @Override
    public Collection<V> values() { throw new IllegalStateException(); }

    @Override
    public Set<Entry<K, V>> entrySet() { throw new IllegalStateException(); }
}