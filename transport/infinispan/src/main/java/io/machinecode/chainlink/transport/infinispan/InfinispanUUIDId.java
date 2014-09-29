package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.transport.core.DistributedUUIDId;
import org.infinispan.remoting.transport.Address;

import java.util.UUID;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanUUIDId extends DistributedUUIDId<Address> {

    public InfinispanUUIDId(final UUID uuid, final Address address) {
        super(uuid, address);
    }

    public InfinispanUUIDId(final Address address) {
        super(address);
    }
}
