package io.machinecode.chainlink.jsl.core.util;

import gnu.trove.function.TObjectFunction;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ForwardingMap<K,V> implements TMap<K,V> {

    protected final TMap<K, V> delegate;

    public ForwardingMap(final TMap<K, V> delegate) {
        this.delegate = delegate;
    }

    public V putIfAbsent(final K key, final V value) {
        return delegate.putIfAbsent(key, value);
    }

    public boolean forEachKey(final TObjectProcedure<? super K> procedure) {
        return delegate.forEachKey(procedure);
    }

    public boolean forEachValue(final TObjectProcedure<? super V> procedure) {
        return delegate.forEachValue(procedure);
    }

    public boolean forEachEntry(final TObjectObjectProcedure<? super K, ? super V> procedure) {
        return delegate.forEachEntry(procedure);
    }

    public boolean retainEntries(final TObjectObjectProcedure<? super K, ? super V> procedure) {
        return delegate.retainEntries(procedure);
    }

    public void transformValues(final TObjectFunction<V, V> function) {
        delegate.transformValues(function);
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return delegate.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return delegate.containsValue(value);
    }

    @Override
    public V get(final Object key) {
        return delegate.get(key);
    }

    public V put(final K key, final V value) {
        return delegate.put(key, value);
    }

    @Override
    public V remove(final Object key) {
        return delegate.remove(key);
    }

    public void putAll(final Map<? extends K, ? extends V> m) {
        delegate.putAll(m);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public Set<K> keySet() {
        return delegate.keySet();
    }

    @Override
    public Collection<V> values() {
        return delegate.values();
    }

    @Override
    public Set<Entry<K,V>> entrySet() {
        return delegate.entrySet();
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }
}
