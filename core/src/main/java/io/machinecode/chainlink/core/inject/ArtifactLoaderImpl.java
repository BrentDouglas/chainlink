package io.machinecode.chainlink.core.inject;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import java.util.Collections;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ArtifactLoaderImpl implements ArtifactLoader {

    private final JarBatchArtifactLoader jarBatchLoader;
    private final WarBatchArtifactLoader warBatchLoader;
    private final TcclArtifactLoader tcclLoader;
    private final ClassLoaderArtifactLoader configuredLoader;
    private final TLinkedHashSet<ArtifactLoader> loaders;

    public ArtifactLoaderImpl(final ClassLoader classLoader, final ArtifactLoader... artifactLoaders) {
        this.jarBatchLoader = new JarBatchArtifactLoader(classLoader);
        this.warBatchLoader = new WarBatchArtifactLoader(classLoader);
        this.configuredLoader = new ClassLoaderArtifactLoader();
        this.tcclLoader = new TcclArtifactLoader();
        this.loaders = new TLinkedHashSet<>();
        Collections.addAll(this.loaders, artifactLoaders);
    }


    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        // 1. Provided Loaders
        for (final ArtifactLoader artifactLoader : this.loaders) {
            final T that = artifactLoader.load(id, as, loader);
            if (that != null) {
                return that;
            }
        }
        // 2. Archive Loader
        final T jar = this.jarBatchLoader.load(id, as, loader);
        if (jar != null) {
            return jar;
        }
        final T war = this.warBatchLoader.load(id, as, loader);
        if (war != null) {
            return war;
        }
        final T configured = this.configuredLoader.load(id, as, loader);
        if (configured != null) {
            return configured;
        }
        // 3. Tccl Loader
        return this.tcclLoader.load(id, as, loader);
    }
}
