package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
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
        final InfinispanRegistry registry = cr.getGlobalComponentRegistry().getComponent(InfinispanRegistry.class);
        initializer.init(registry);
    }
}
