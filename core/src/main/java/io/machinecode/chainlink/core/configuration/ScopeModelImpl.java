package io.machinecode.chainlink.core.configuration;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.ScopeModel;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ScopeModelImpl implements ScopeModel {

    final WeakReference<ClassLoader> loader;

    final LinkedHashMap<String, DeclarationImpl<ArtifactLoader>> artifactLoaders = new LinkedHashMap<>();
    final Map<String, JobOperatorModelImpl> jobOperators = new THashMap<>();
    final Map<String, ConfigurationImpl> configurations = new THashMap<>();
    final Set<String> names;
    final ScopeModelImpl parent;
    boolean loadedConfigurations = false;
    final ClassLoaderDependencies _dependencies;
    private transient ArtifactLoader _artifactLoader;
    private transient ArtifactLoader[] _artifactLoaders;

    public ScopeModelImpl(final WeakReference<ClassLoader> loader, final Set<String> names) {
        this(loader, names, null);
    }

    public ScopeModelImpl(final ScopeModelImpl parent) {
        this(parent.loader, parent.names, parent);
    }

    public ScopeModelImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final ScopeModelImpl parent) {
        this.loader = loader;
        this.names = names;
        this.parent = parent;
        this._dependencies = new ClassLoaderDependencies(this.loader);
    }

    @Override
    public Declaration<ArtifactLoader> getArtifactLoader(final String name) {
        DeclarationImpl<ArtifactLoader> artifactLoader = artifactLoaders.get(name);
        if (artifactLoader != null) {
            return artifactLoader;
        }
        if (names.contains(name)) {
            throw new IllegalStateException("Resource already declared for name: " + name); //TODO Message and better exception
        }
        artifactLoader = new DeclarationImpl<>(loader, names, new THashMap<String, DeclarationImpl<?>>(), ArtifactLoader.class, ArtifactLoaderFactory.class, name);
        _artifactLoader = null;
        _artifactLoaders = null;
        artifactLoaders.put(name, artifactLoader);
        return artifactLoader;
    }

    @Override
    public JobOperatorModelImpl getJobOperator(final String name) {
        JobOperatorModelImpl scope = jobOperators.get(name);
        if (scope == null) {
            if (names.contains(name)) {
                throw new RuntimeException("operator " + name + " already declared."); //TODO Message, better exception
            }
            jobOperators.put(name, scope = new JobOperatorModelImpl(name, this, loader));
        }
        return scope;
    }

    public Map<String, JobOperatorModelImpl> getJobOperators() {
        return jobOperators;
    }

    public ConfigurationImpl getConfiguration(final String name) throws Exception {
        return getConfiguration(name, null);
    }

    public ConfigurationImpl getConfiguration(final String name, final ArtifactLoader loader) throws Exception {
        if (!loadedConfigurations) {
            final ArtifactLoader artifactLoader = loader == null
                    ? getConfigurationArtifactLoader()
                    : getConfigurationArtifactLoader(loader);
            for (final Map.Entry<String, JobOperatorModelImpl> entry : jobOperators.entrySet()) {
                this.configurations.put(entry.getKey(), new ConfigurationImpl(entry.getValue(), artifactLoader));
            }
            this.loadedConfigurations = true;
        }
        return this.configurations.get(name);
    }

    public ArtifactLoader getConfigurationArtifactLoader() {
        if (this._artifactLoader == null) {
            this._artifactLoader = new ConfigurationArtifactLoader(this._artifactLoaders());
        }
        return this._artifactLoader;
    }

    public ArtifactLoader getConfigurationArtifactLoader(final ArtifactLoader loader) {
        if (this._artifactLoader == null) {
            this._artifactLoader = new ConfigurationArtifactLoader(loader, this._artifactLoaders());
        }
        return this._artifactLoader;
    }

    ArtifactLoader[] _artifactLoaders() {
        if (this._artifactLoaders != null) {
            return this._artifactLoaders;
        }
        final ArtifactLoader init;
        int i = 0;
        if (parent == null) {
            init = new ConfigurationArtifactLoader();
            this._artifactLoaders = new ArtifactLoader[this.artifactLoaders.size()];
        } else {
            final ArtifactLoader[] pal = this.parent._artifactLoaders();
            init = parent.getConfigurationArtifactLoader();
            this._artifactLoaders = new ArtifactLoader[pal.length + this.artifactLoaders.size()];
            System.arraycopy(pal, 0, this._artifactLoaders, 0, pal.length);
            i = pal.length;
        }
        for (final DeclarationImpl<ArtifactLoader> dec : this.artifactLoaders.values()) {
            this._artifactLoaders[i++] = dec.get(_dependencies, init);
        }
        return this._artifactLoaders;
    }
}
