package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ChainlinkCommand {

    void setTransport(final InfinispanTransport transport);
}
