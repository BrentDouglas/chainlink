package io.machinecode.chainlink.core.configuration;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;

import java.util.Collections;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationLoaderImpl implements ConfigurationLoader {

    private final ClassLoaderConfigurationLoader configuredLoader;
    private final TLinkedHashSet<ConfigurationLoader> loaders;

    public ConfigurationLoaderImpl(final ConfigurationLoader extra, final ConfigurationLoader... loaders) {
        this();
        this.loaders.add(extra);
        Collections.addAll(this.loaders, loaders);
    }

    public ConfigurationLoaderImpl(final ConfigurationLoader... loaders) {
        this();
        Collections.addAll(this.loaders, loaders);
    }

    public ConfigurationLoaderImpl() {
        this.configuredLoader = new ClassLoaderConfigurationLoader();
        this.loaders = new TLinkedHashSet<>();
    }


    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        for (final ConfigurationLoader configurationLoader : this.loaders) {
            final T that = configurationLoader.load(id, as, loader);
            if (that != null) {
                return that;
            }
        }
        return this.configuredLoader.load(id, as, loader);
    }
}
