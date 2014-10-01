package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.Filter;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class AllEntriesFilter implements Filter, Serializable {
    @Override
    public boolean evaluate(final Object o) {
        return true;
    }
}
