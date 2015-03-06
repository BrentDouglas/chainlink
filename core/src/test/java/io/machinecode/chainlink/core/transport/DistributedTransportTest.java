package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.artifacts.TestTransport;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.transport.Transport;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DistributedTransportTest extends TransportTest {

    private static final ConcurrentMap<String, TestTransport> transports = new ConcurrentHashMap<>();

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "first", Arrays.asList("second", "third"), dependencies, properties);
                transports.put("first", transport);
                return transport;
            }
        });
    }

    @Override
    protected void visitSecondJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "second", Arrays.asList("first", "third"), dependencies, properties);
                transports.put("second", transport);
                return transport;
            }
        });
    }

    @Override
    protected void visitThirdJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getTransport().setFactory(new TransportFactory() {
            @Override
            public Transport produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
                final TestTransport transport = new TestTransport(transports, "third", Arrays.asList("first", "second"), dependencies, properties);
                transports.put("third", transport);
                return transport;
            }
        });
    }

    protected TestTransportFactory createFactory() {
        return null;
    }
}
