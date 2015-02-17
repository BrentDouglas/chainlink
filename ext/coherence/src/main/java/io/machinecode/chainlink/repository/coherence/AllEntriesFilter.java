package io.machinecode.chainlink.repository.coherence;

import com.tangosol.util.Filter;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class AllEntriesFilter implements Filter, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public boolean evaluate(final Object o) {
        return true;
    }
}
