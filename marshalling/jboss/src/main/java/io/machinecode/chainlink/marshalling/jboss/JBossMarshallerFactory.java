package io.machinecode.chainlink.marshalling.jboss;

import io.machinecode.chainlink.spi.configuration.BaseConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.MarshallerFactory;
import io.machinecode.chainlink.spi.marshalling.Marshaller;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.river.RiverMarshallerFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JBossMarshallerFactory implements MarshallerFactory {
    @Override
    public Marshaller produce(final BaseConfiguration configuration) throws Exception {
        return new JBossMarshaller(new RiverMarshallerFactory(), new MarshallingConfiguration());
    }
}
