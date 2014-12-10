package io.machinecode.chainlink.marshalling.jboss;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.cloner.ClonerConfiguration;
import org.jboss.marshalling.river.RiverMarshallerFactory;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JbossMarshallingFactory implements MarshallingFactory {
    @Override
    public Marshalling produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new JbossMarshalling(new RiverMarshallerFactory(), new MarshallingConfiguration(), new ClonerConfiguration());
    }
}
