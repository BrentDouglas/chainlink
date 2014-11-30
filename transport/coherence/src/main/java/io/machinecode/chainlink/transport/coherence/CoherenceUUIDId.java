package io.machinecode.chainlink.transport.coherence;

import com.tangosol.net.Member;
import io.machinecode.chainlink.transport.core.DistributedUUIDId;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CoherenceUUIDId extends DistributedUUIDId<Member> {

    public CoherenceUUIDId(final UUID uuid, final Member address) {
        super(uuid, address);
    }

    public CoherenceUUIDId(final Member address) {
        super(address);
    }
}
