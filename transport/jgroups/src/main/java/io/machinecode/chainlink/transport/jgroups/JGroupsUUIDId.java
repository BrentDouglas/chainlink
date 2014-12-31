package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.core.transport.DistributedUUIDId;
import org.jgroups.Address;

import java.util.UUID;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsUUIDId extends DistributedUUIDId<Address> {
    private static final long serialVersionUID = 1L;

    public JGroupsUUIDId(final UUID uuid, final Address address) {
        super(uuid, address);
    }

    public JGroupsUUIDId(final Address address) {
        super(address);
    }
}
