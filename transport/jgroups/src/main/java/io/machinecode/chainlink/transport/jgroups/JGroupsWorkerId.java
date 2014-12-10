package io.machinecode.chainlink.transport.jgroups;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;
import org.jgroups.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsWorkerId extends DistributedWorkerId<Address> {
    private static final long serialVersionUID = 1L;

    public JGroupsWorkerId(final Thread thread, final Address address) {
        super(thread, address);
    }
}
