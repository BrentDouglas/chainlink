package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.transport.core.DistributedWorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastWorkerId extends DistributedWorkerId<Member> {
    private static final long serialVersionUID = 1L;

    public HazelcastWorkerId(final Thread thread, final Member address) {
        super(thread, address);
    }
}
