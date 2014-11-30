package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.transport.core.DistributedUUIDId;
import org.jgroups.Address;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JGroupsUUIDId extends DistributedUUIDId<Address> {

    public JGroupsUUIDId(final UUID uuid, final Address address) {
        super(uuid, address);
    }

    public JGroupsUUIDId(final Address address) {
        super(address);
    }
}
