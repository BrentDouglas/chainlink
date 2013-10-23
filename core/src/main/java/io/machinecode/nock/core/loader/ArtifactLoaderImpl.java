package io.machinecode.nock.core.loader;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.nock.core.batch.loader.JarXmlArtifactLoader;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.loader.ArtifactLoader;

import java.util.Collections;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ArtifactLoaderImpl implements ArtifactLoader {

    private final JarXmlArtifactLoader loader;
    private final Set<ArtifactLoader> loaders;

    public ArtifactLoaderImpl(final Configuration configuration) {
        this.loader = new JarXmlArtifactLoader(configuration.getClassLoader());
        this.loaders = new TLinkedHashSet<ArtifactLoader>();
        Collections.addAll(this.loaders, configuration.getArtifactLoaders());
        //final List<ArtifactLoader> loaders;
        //try {
        //    loaders = new ResolvableService<ArtifactLoader>(ArtifactLoader.class).resolve(configuration.getClassLoader());
        //} catch (final ClassNotFoundException e) {
        //    throw new RuntimeException(e);
        //}
        //this.loaders.addAll(loaders);
    }


    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) {
        // 1. Provided Loaders
        for (final ArtifactLoader artifactLoader : this.loaders) {
            final T that = artifactLoader.load(id, as, loader);
            if (that == null) {
                continue;
            }
            return that;
        }
        // 2. Archive Loader
        return this.loader.load(id, as, loader);
    }
}
