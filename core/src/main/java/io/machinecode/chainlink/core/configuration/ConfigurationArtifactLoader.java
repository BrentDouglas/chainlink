package io.machinecode.chainlink.core.configuration;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.core.inject.ClassLoaderArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import java.util.Collections;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationArtifactLoader implements ArtifactLoader {

    private final ClassLoaderArtifactLoader configuredLoader;
    private final TLinkedHashSet<ArtifactLoader> loaders;

    public ConfigurationArtifactLoader(final ArtifactLoader extra, final ArtifactLoader... loaders) {
        this();
        this.loaders.add(extra);
        Collections.addAll(this.loaders, loaders);
    }

    public ConfigurationArtifactLoader(final ArtifactLoader... loaders) {
        this();
        Collections.addAll(this.loaders, loaders);
    }

    public ConfigurationArtifactLoader() {
        this.configuredLoader = new ClassLoaderArtifactLoader();
        this.loaders = new TLinkedHashSet<>();
    }


    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        for (final ArtifactLoader artifactLoader : this.loaders) {
            final T that = artifactLoader.load(id, as, loader);
            if (that != null) {
                return that;
            }
        }
        return this.configuredLoader.load(id, as, loader);
    }
}
