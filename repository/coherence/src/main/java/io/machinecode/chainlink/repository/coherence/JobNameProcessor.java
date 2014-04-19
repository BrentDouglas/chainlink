package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;
import io.machinecode.chainlink.spi.repository.ExtendedJobInstance;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class JobNameProcessor implements InvocableMap.EntryProcessor, Serializable {
    @Override
    public Object process(final InvocableMap.Entry entry) {
        return ((ExtendedJobInstance) entry.getValue()).getJobName();
    }

    @Override
    public Map processAll(final Set set) {
        return null;
    }
}
