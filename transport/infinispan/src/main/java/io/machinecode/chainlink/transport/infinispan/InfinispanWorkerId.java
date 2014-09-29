package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InfinispanWorkerId extends DistributedWorkerId<Address> {

    public InfinispanWorkerId(final Thread thread, final Address address) {
        super(thread, address);
    }
}
