package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import javax.enterprise.inject.spi.Extension;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JndiCdiArtifactLoaderFactory implements ArtifactLoaderFactory, Extension {

    @Override
    public ArtifactLoader produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new CdiArtifactLoader(new JndiBeanManagerLookup());
    }
}
