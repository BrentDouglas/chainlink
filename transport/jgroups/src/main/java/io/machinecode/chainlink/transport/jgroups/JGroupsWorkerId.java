package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;
import org.jgroups.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsWorkerId extends DistributedWorkerId<Address> {

    public JGroupsWorkerId(final Thread thread, final Address address) {
        super(thread, address);
    }
}
