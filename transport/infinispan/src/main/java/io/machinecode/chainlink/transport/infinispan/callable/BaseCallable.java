package io.machinecode.chainlink.transport.infinispan.callable;

import org.infinispan.AdvancedCache;
import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

import java.io.Serializable;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class BaseCallable<K,V,T> implements DistributedCallable<K,V,T>, Serializable {

    protected AdvancedCache<K,V> cache;

    @Override
    public void setEnvironment(final Cache<K,V> cache, final Set<K> longs) {
        this.cache = cache.getAdvancedCache();
    }
}
