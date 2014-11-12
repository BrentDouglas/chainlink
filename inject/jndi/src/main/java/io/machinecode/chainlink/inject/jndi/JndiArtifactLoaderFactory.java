package io.machinecode.chainlink.inject.jndi;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JndiArtifactLoaderFactory implements ArtifactLoaderFactory {
    @Override
    public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return new JndiArtifactLoader();
    }
}
