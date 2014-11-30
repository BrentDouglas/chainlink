package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.Filter;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class AllEntriesFilter implements Filter, Serializable {
    @Override
    public boolean evaluate(final Object o) {
        return true;
    }
}
