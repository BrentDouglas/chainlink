package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;
import org.jgroups.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JGroupsWorkerId extends DistributedWorkerId<Address> {

    public JGroupsWorkerId(final Thread thread, final Address address) {
        super(thread, address);
    }
}
