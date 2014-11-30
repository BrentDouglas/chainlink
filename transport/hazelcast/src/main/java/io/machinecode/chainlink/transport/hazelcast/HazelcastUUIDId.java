package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.transport.core.DistributedUUIDId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class HazelcastUUIDId extends DistributedUUIDId<Member> {

    public HazelcastUUIDId(final UUID uuid, final Member address) {
        super(uuid, address);
    }

    public HazelcastUUIDId(final Member address) {
        super(address);
    }
}
