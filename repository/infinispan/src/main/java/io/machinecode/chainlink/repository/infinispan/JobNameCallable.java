package io.machinecode.chainlink.repository.infinispan;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobNameCallable extends BaseCallable<Long, ExtendedJobInstance, Set<String>> {

    @Override
    public Set<String> call() throws Exception {
        final Set<String> ret = new THashSet<String>();
        for (final ExtendedJobInstance jobInstance : cache.values()) {
            ret.add(jobInstance.getJobName());
        }
        return ret;
    }
}
