package io.machinecode.nock.core.loader;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.nock.core.batch.loader.JarBatchArtifactLoader;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.ArtifactOfWrongTypeException;

import java.util.Collections;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ArtifactLoaderImpl implements ArtifactLoader {

    private final JarBatchArtifactLoader batchLoader;
    private final TcclArtifactLoader tcclLoader;
    private final ClassLoaderArtifactLoader configuredLoader;
    private final TLinkedHashSet<ArtifactLoader> loaders;

    public ArtifactLoaderImpl(final Configuration configuration) {
        this.batchLoader = new JarBatchArtifactLoader(configuration.getClassLoader());
        this.tcclLoader = new TcclArtifactLoader();
        this.loaders = new TLinkedHashSet<ArtifactLoader>();
        this.configuredLoader = new ClassLoaderArtifactLoader();
        Collections.addAll(this.loaders, configuration.getArtifactLoaders());
    }


    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws ArtifactOfWrongTypeException {
        // 1. Provided Loaders
        for (final ArtifactLoader artifactLoader : this.loaders) {
            final T that = artifactLoader.load(id, as, loader);
            if (that != null) {
                return that;
            }
        }
        // 2. Archive Loader
        final T batch = this.batchLoader.load(id, as, loader);
        if (batch != null) {
            return batch;
        }
        final T configured = this.configuredLoader.load(id, as, loader);
        if (configured != null) {
            return configured;
        }
        // 3. Tccl Loader
        return this.tcclLoader.load(id, as, loader);
    }
}
