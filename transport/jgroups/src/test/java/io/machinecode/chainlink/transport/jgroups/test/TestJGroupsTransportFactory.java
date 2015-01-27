package io.machinecode.chainlink.transport.jgroups.test;

import io.machinecode.chainlink.core.transport.TestTransportFactory;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.jgroups.JGroupsTransport;
import org.jgroups.JChannel;
import org.jgroups.util.Util;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestJGroupsTransportFactory implements TestTransportFactory {

    final JChannel channel;

    public TestJGroupsTransportFactory() throws Exception {
        channel = new JChannel(Util.getTestStack());
        channel.connect("chainlink-jgroups-test");
    }

    @Override
    public Transport produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new JGroupsTransport(
                dependencies,
                properties,
                channel
        );
    }

    @Override
    public void close() {
        channel.close();
    }
}
