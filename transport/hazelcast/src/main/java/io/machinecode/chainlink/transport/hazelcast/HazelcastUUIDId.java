package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.transport.core.DistributedUUIDId;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class HazelcastUUIDId extends DistributedUUIDId<Member> {

    public HazelcastUUIDId(final UUID uuid, final Member address) {
        super(uuid, address);
    }

    public HazelcastUUIDId(final Member address) {
        super(address);
    }
}
