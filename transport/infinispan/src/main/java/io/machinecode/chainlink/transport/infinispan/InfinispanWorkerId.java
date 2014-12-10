package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.transport.core.DistributedWorkerId;
import org.infinispan.remoting.transport.Address;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InfinispanWorkerId extends DistributedWorkerId<Address> {
    private static final long serialVersionUID = 1L;

    public InfinispanWorkerId(final Thread thread, final Address address) {
        super(thread, address);
    }
}
