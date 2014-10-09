package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.TransportConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JGroupsTransportFactory implements TransportFactory {

    @Override
    public JGroupsTransport produce(final TransportConfiguration configuration) throws Exception {
        final JChannel channel = new JChannel("tck-udp.xml");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                channel.close();
            }
        });
        return new JGroupsTransport(
                configuration,
                channel,
                "chainlink-jgroups-tck"
        );
    }
}
