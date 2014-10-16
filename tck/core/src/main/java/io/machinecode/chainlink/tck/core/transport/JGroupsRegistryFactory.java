package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.RegistryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.JChannel;

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
