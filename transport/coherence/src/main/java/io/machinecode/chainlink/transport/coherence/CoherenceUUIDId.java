package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.transport.core.DistributedUUIDId;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CoherenceUUIDId extends DistributedUUIDId<Member> {

    public CoherenceUUIDId(final UUID uuid, final Member address) {
        super(uuid, address);
    }

    public CoherenceUUIDId(final Member address) {
        super(address);
    }
}
