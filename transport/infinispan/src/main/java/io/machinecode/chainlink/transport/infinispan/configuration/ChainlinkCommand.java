package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ChainlinkCommand {

    void init(final InfinispanRegistry registry);
}
