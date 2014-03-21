package io.machinecode.chainlink.repository.infinispan;

import org.infinispan.Cache;
import org.infinispan.distexec.DistributedCallable;

import java.io.Serializable;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public abstract class BaseCallable<K,V,T> implements DistributedCallable<K,V,T>, Serializable {

    protected Cache<K,V> cache;

    @Override
    public void setEnvironment(final Cache<K,V> cache, final Set<K> longs) {
        this.cache = cache;
    }
}
