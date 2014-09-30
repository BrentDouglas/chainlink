package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.transport.core.DistributedWorkerId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceWorkerId extends DistributedWorkerId<Member> {

    public CoherenceWorkerId(final Thread thread, final Member address) {
        super(thread, address);
    }
}
