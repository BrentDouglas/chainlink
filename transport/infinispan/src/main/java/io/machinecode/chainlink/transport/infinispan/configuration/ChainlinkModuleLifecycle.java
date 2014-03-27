package io.machinecode.chainlink.transport.infinispan.configuration;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import org.infinispan.factories.ComponentRegistry;
import org.infinispan.lifecycle.AbstractModuleLifecycle;

/**
 * From ServiceLoader
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChainlinkModuleLifecycle extends AbstractModuleLifecycle {
    @Override
    public void cacheStarted(final ComponentRegistry registry, final String cacheName) {
        final ChainlinkModuleCommandInitializer initializer = registry.getComponent(ChainlinkModuleCommandInitializer.class);
        final InfinispanTransport transport = registry.getGlobalComponentRegistry().getComponent(InfinispanTransport.class);
        initializer.setTransport(transport);
    }
}
