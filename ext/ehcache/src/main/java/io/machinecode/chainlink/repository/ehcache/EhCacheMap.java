package io.machinecode.chainlink.repository.ehcache;

import io.machinecode.chainlink.core.repository.CacheMap;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class EhCacheMap<K,V> extends CacheMap<K,V> {

    final Ehcache cache;

    public EhCacheMap(final Ehcache cache) {
        this.cache = cache;
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.cache.isKeyInCache(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.cache.isValueInCache(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(final Object key) {
        final Element element = this.cache.get(key);
        return element == null ? null : (V) element.getObjectValue();
    }

    @Override
    public V put(final K key, final V value) {
        this.cache.put(new Element(key, value));
        return null;
    }

    @Override
    public V remove(final Object key) {
        this.cache.remove(key);
        return null;
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        for (final Entry<? extends K, ? extends V> e : m.entrySet()) {
            this.cache.put(new Element(e.getKey(), e.getValue()));
        }
    }

    public static <K,V> EhCacheMap<K,V> with(final Ehcache cache) {
        return new EhCacheMap<>(cache);
    }
}
