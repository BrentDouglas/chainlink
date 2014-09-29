package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.LockingMode;
import org.infinispan.transaction.TransactionMode;
import org.infinispan.transaction.lookup.TransactionManagerLookup;
import org.infinispan.util.concurrent.IsolationLevel;
import org.jgroups.Channel;
import org.jgroups.ChannelListener;
import org.jgroups.JChannel;

import javax.transaction.TransactionManager;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JGroupsRegistryFactory implements RegistryFactory {

    @Override
    public JGroupsRegistry produce(final RegistryConfiguration configuration) throws Exception {
        final JChannel channel = new JChannel("tck-udp.xml");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                channel.close();
            }
        });
        return new JGroupsRegistry(
                configuration,
                channel,
                "chainlink-jgroups-tck"
        );
    }
}
