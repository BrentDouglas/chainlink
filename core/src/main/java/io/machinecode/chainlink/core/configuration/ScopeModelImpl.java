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

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.inject.ClosableScopeImpl;
import io.machinecode.chainlink.core.property.ArrayPropertyLookup;
import io.machinecode.chainlink.core.property.SinglePropertyLookup;
import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.ScopeModel;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationLoaderFactory;
import io.machinecode.chainlink.spi.property.PropertyLookup;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ScopeModelImpl extends PropertyModelImpl implements ScopeModel, AutoCloseable {

    final WeakReference<ClassLoader> loader;
    final Map<String, JobOperatorModelImpl> jobOperators = new THashMap<>();
    final Map<String, ConfigurationImpl> configurations = new THashMap<>();
    final Set<String> names;
    final ScopeModelImpl parent;
    boolean loadedConfigurations = false;
    final ClassLoaderDependencies _dependencies;
    final ClosableScopeImpl scope;
    private transient ConfigurationLoader _configurationLoader;
    private transient ConfigurationLoader[] _configurationLoaders;
    private transient PropertyLookup _lookup;

    final ListModelImpl<ConfigurationLoader> configurationLoaders = new ListModelImpl<ConfigurationLoader>() {
        @Override
        protected DeclarationImpl<ConfigurationLoader> create() {
            return new DeclarationImpl<>(loader, ConfigurationLoader.class, ConfigurationLoaderFactory.class);
        }
    };

    public ScopeModelImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final ClosableScopeImpl scope) {
        this(loader, names, null, scope);
    }

    public ScopeModelImpl(final ScopeModelImpl parent, final ClosableScopeImpl scope) {
        this(parent.loader, parent.names, parent, scope);
    }

    public ScopeModelImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final ScopeModelImpl parent, final ClosableScopeImpl scope) {
        this.loader = loader;
        this.names = names;
        this.parent = parent;
        this.scope = scope;
        this._dependencies = new ClassLoaderDependencies(this.scope, this.loader);
    }

    @Override
    public ListModelImpl<ConfigurationLoader> getConfigurationLoaders() {
        _configurationLoader = null;
        _configurationLoaders = null;
        return configurationLoaders;
    }

    @Override
    public JobOperatorModelImpl getJobOperator(final String name) {
        if (name == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        JobOperatorModelImpl operator = jobOperators.get(name);
        if (operator == null) {
            if (names.contains(name)) {
                throw new RuntimeException("operator " + name + " already declared."); //TODO Message, better exception
            }
            jobOperators.put(name, operator = new JobOperatorModelImpl(name, this, loader));
        }
        return operator;
    }

    public ClassLoader getClassLoader() {
        return this.loader.get();
    }

    public Map<String, JobOperatorModelImpl> getJobOperators() {
        return jobOperators;
    }

    public ConfigurationImpl getConfiguration(final String name) throws Exception {
        return getConfiguration(name, null);
    }

    public ConfigurationImpl getConfiguration(final String name, final ConfigurationLoader loader) throws Exception {
        if (!loadedConfigurations) {
            final ConfigurationLoader configurationLoader = loader == null
                    ? getConfigurationLoader()
                    : getConfigurationLoader(loader);
            for (final Map.Entry<String, JobOperatorModelImpl> entry : jobOperators.entrySet()) {
                this.configurations.put(entry.getKey(), new ConfigurationImpl(entry.getValue(), configurationLoader, this.scope));
            }
            this.loadedConfigurations = true;
        }
        return this.configurations.get(name);
    }

    public ConfigurationLoader getConfigurationLoader() {
        if (this._configurationLoader == null) {
            this._configurationLoader = new ConfigurationLoaderImpl(this._configurationLoaders());
        }
        return this._configurationLoader;
    }

    public ConfigurationLoader getConfigurationLoader(final ConfigurationLoader loader) {
        if (this._configurationLoader == null) {
            this._configurationLoader = new ConfigurationLoaderImpl(loader, this._configurationLoaders());
        }
        return this._configurationLoader;
    }

    ConfigurationLoader[] _configurationLoaders() {
        if (this._configurationLoaders != null) {
            return this._configurationLoaders;
        }
        final ConfigurationLoader init;
        int i = 0;
        if (parent == null) {
            init = new ConfigurationLoaderImpl();
            this._configurationLoaders = new ConfigurationLoader[this.configurationLoaders.size()];
        } else {
            final ConfigurationLoader[] pal = this.parent._configurationLoaders();
            init = parent.getConfigurationLoader();
            this._configurationLoaders = new ConfigurationLoader[pal.length + this.configurationLoaders.size()];
            System.arraycopy(pal, 0, this._configurationLoaders, 0, pal.length);
            i = pal.length;
        }
        if (this._lookup == null) {
            if (this.parent == null) {
                this._lookup = new SinglePropertyLookup(this.properties);
            } else {
                //TODO Maybe make this more generic
                this._lookup = new ArrayPropertyLookup(this.properties, this.parent.properties);
            }
        }
        for (final DeclarationImpl<ConfigurationLoader> dec : this.configurationLoaders) {
            this._configurationLoaders[i++] = dec.get(_dependencies, this._lookup, init);
        }
        return this._configurationLoaders;
    }

    @Override
    public void close() throws Exception {
        this.scope.close();
    }
}
