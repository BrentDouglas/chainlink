package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.spi.configuration.Configuration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ChainlinkCommand {

    void init(final Configuration configuration);
}
