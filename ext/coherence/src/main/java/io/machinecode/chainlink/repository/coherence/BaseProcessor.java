package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.InvocableMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public abstract class BaseProcessor implements InvocableMap.EntryProcessor, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public Map processAll(final Set set) {
        final Map<Object, Object> ret = new HashMap<>(set.size());
        for (final Object value : set) {
            final InvocableMap.Entry entry = (InvocableMap.Entry)value;
            final Object out  = process(entry);
            if (out != null) {
                ret.put(entry.getKey(), out);
            }
        }
        return ret;
    }
}
