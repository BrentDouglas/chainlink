package io.machinecode.chainlink.tck.core.transport;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JGroupsTransportFactory implements TransportFactory {

    @Override
    public JGroupsTransport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        final JChannel channel = new JChannel("tck-udp.xml");
        channel.connect("chainlink-jgroups-tck");
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                channel.close();
            }
        });
        return new JGroupsTransport(
                dependencies,
                properties,
                channel
        );
    }
}
