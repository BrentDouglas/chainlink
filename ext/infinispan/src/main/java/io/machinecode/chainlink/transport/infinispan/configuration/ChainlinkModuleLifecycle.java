package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.spi.configuration.Configuration;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.lifecycle.AbstractModuleLifecycle;

/**
 * From ServiceLoader
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class ChainlinkModuleLifecycle extends AbstractModuleLifecycle {
    @Override
    public void cacheStarted(final ComponentRegistry cr, final String cacheName) {
        final ChainlinkModuleCommandInitializer initializer = cr.getComponent(ChainlinkModuleCommandInitializer.class);
        final Configuration configuration = cr.getGlobalComponentRegistry().getComponent(Configuration.class);
        initializer.init(configuration);
    }
}
