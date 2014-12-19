package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.transport.core.DistributedWorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceWorkerId extends DistributedWorkerId<Member> {
    private static final long serialVersionUID = 1L;

    public CoherenceWorkerId(final Thread thread, final Member address) {
        super(thread, address);
    }
}
