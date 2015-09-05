/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
