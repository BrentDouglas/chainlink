package io.machinecode.chainlink.tck.core.loader;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoOpArtifactLoaderFactory implements ArtifactLoaderFactory {
    @Override
    public ArtifactLoader produce(final Dependencies dependencies, final PropertyLookup properties) {
        return new NoOpArtifactLoader();
    }

    public static class NoOpArtifactLoader implements ArtifactLoader {
        @Override
        public <T> T load(final String id, final Class<T> as, final ClassLoader loader) {
            return null;
        }
    }
}
