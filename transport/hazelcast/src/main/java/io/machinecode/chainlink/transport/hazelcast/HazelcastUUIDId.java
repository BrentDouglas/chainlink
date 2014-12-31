package io.machinecode.chainlink.transport.hazelcast;

import com.hazelcast.core.Member;
import io.machinecode.chainlink.core.transport.DistributedUUIDId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class HazelcastUUIDId extends DistributedUUIDId<Member> {
    private static final long serialVersionUID = 1L;

    public HazelcastUUIDId(final UUID uuid, final Member address) {
        super(uuid, address);
    }

    public HazelcastUUIDId(final Member address) {
        super(address);
    }
}
