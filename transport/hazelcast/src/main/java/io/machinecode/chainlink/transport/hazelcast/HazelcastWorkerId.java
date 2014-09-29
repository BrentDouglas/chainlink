package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.transport.core.DistributedWorkerId;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastWorkerId extends DistributedWorkerId<Member> {

    public HazelcastWorkerId(final Thread thread, final Member address) {
        super(thread, address);
    }
}
