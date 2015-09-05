/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.repository.gridgain;

import io.machinecode.chainlink.core.repository.CacheMap;
import org.gridgain.grid.GridException;
import org.gridgain.grid.cache.GridCache;

import javax.batch.operations.BatchRuntimeException;
import java.util.Map;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
@SuppressWarnings("unchecked")
public class GridGainMap<K,V> extends CacheMap<K,V> {

    final GridCache<K,V> cache;

    public GridGainMap(final GridCache<K,V> cache) {
        this.cache = cache;
    }

    @Override
    public boolean containsKey(final Object key) {
        return this.cache.containsKey((K)key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return this.cache.containsValue((V)value);
    }

    @Override
    public V get(final Object key) {
        try {
            return this.cache.get((K)key);
        } catch (final GridException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public V put(final K key, final V value) {
        try {
            if (!this.cache.putx(key, value)) {
                throw new BatchRuntimeException(); //TODO Message
            }
            return null;
        } catch (final GridException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public V remove(final Object key) {
        try {
            if (!this.cache.removex((K)key)) {
                throw new BatchRuntimeException(); //TODO Message
            }
            return null;
        } catch (final GridException e) {
            throw new BatchRuntimeException(e);
        }
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m) {
        try {
            this.cache.putAll(m);
        } catch (final GridException e) {
            throw new BatchRuntimeException(e);
        }
    }

    public static <K,V> GridGainMap<K,V> with(final GridCache<K,V> cache) {
        return new GridGainMap<>(cache);
    }
}
