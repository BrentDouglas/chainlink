package io.machinecode.chainlink.marshalling.jboss;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.marshalling.MarshallingProvider;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.cloner.ClonerConfiguration;
import org.jboss.marshalling.river.RiverMarshallerFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JbossMarshallingProviderFactory implements MarshallingProviderFactory {
    @Override
    public MarshallingProvider produce(final Configuration configuration) throws Exception {
        return new JbossMarshallingProvider(new RiverMarshallerFactory(), new MarshallingConfiguration(), new ClonerConfiguration());
    }
}
