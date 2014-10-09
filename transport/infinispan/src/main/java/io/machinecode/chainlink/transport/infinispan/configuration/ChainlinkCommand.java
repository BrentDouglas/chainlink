package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ChainlinkCommand {

    void init(final InfinispanTransport transport);
}
