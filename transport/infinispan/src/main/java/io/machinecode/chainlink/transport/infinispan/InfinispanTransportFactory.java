package io.machinecode.chainlink.transport.infinispan;

import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.transport.Transport;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.rpc.RpcOptions;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class InfinispanTransportFactory implements TransportFactory {

    public abstract EmbeddedCacheManager getCacheManager(final TransportConfiguration configuration);

    public abstract RpcOptions getRpcOptions();

    @Override
    public final Transport produce(final TransportConfiguration configuration) throws Exception {
        final EmbeddedCacheManager manager = getCacheManager(configuration);
        final InfinispanTransport executor = new InfinispanTransport(
                configuration,
                manager,
                getRpcOptions()
        );
        manager.getGlobalComponentRegistry().registerComponent(executor, InfinispanTransport.class);
        return executor;
    }
}
