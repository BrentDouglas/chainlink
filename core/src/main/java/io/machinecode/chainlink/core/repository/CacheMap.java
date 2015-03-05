package io.machinecode.chainlink.core.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class CacheMap<K,V> implements Map<K,V> {

    @Override
    public int size() { throw new UnsupportedOperationException(); }

    @Override
    public boolean isEmpty() { throw new UnsupportedOperationException(); }

    @Override
    public void clear() { throw new UnsupportedOperationException(); }

    @Override
    public Set<K> keySet() { throw new UnsupportedOperationException(); }

    @Override
    public Collection<V> values() { throw new UnsupportedOperationException(); }

    @Override
    public Set<Entry<K, V>> entrySet() { throw new UnsupportedOperationException(); }
}
