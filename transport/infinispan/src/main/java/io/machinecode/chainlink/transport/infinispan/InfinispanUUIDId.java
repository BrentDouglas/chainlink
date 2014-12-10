package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.transport.core.DistributedUUIDId;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanUUIDId extends DistributedUUIDId<Address> {
    private static final long serialVersionUID = 1L;

    public InfinispanUUIDId(final UUID uuid, final Address address) {
        super(uuid, address);
    }

    public InfinispanUUIDId(final Address address) {
        super(address);
    }
}
