package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.jgroups.JGroupsTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import org.jgroups.JChannel;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsTransportFactory implements TransportFactory {

    @Override
    public JGroupsTransport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        final JChannel channel = new JChannel("tck-udp.xml");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                channel.close();
            }
        });
        return new JGroupsTransport(
                dependencies,
                properties,
                channel,
                "chainlink-jgroups-tck"
        );
    }
}
