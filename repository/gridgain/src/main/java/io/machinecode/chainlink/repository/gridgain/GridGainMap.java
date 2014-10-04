package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.repository.core.CacheMap;
import org.gridgain.grid.GridException;
import org.gridgain.grid.cache.GridCache;

import java.util.Map;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class GridGainMap<K,V> extends CacheMap<K,V> {

    final GridCache<K,V> cache;

    public GridGainMap(final GridCache<K,V> cache) {
        this.cache = cache;
    }

    @Override
    public V get(final Object key) {
        try {
            return this.cache.get((K)key);
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V put(final K key, final V value) {
        try {
            return this.cache.put(key, value);
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public V remove(final Object key) {
        try {
            return this.cache.remove((K)key);
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        try {
            this.cache.putAll(m);
        } catch (GridException e) {
            throw new RuntimeException(e);
        }
    }

    public static <K,V> GridGainMap<K,V> with(final GridCache<K,V> cache) {
        return new GridGainMap<K, V>(cache);
    }
}
