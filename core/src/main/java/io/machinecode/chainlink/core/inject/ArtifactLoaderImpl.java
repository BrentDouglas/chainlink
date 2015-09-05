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
package io.machinecode.chainlink.core.inject;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.security.AccessController;
import java.util.Collections;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ArtifactLoaderImpl implements ArtifactLoader {

    private final BatchArtifactLoader jarBatchLoader;
    private final BatchArtifactLoader warBatchLoader;
    private final TcclArtifactLoader tcclLoader;
    private final ClassLoaderArtifactLoader configuredLoader;
    private final TLinkedHashSet<ArtifactLoader> loaders;

    public ArtifactLoaderImpl(final ClassLoader classLoader, final ArtifactLoader... artifactLoaders) throws JAXBException, IOException {
        final InjectablesProvider provider = loadProvider();
        this.jarBatchLoader = new BatchArtifactLoader("META-INF/", classLoader, provider);
        this.warBatchLoader = new BatchArtifactLoader("WEB-INF/classes/META-INF/", classLoader, provider);
        this.configuredLoader = new ClassLoaderArtifactLoader(provider);
        this.tcclLoader = new TcclArtifactLoader(provider);
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

    public static InjectablesProvider loadProvider() {
        final ServiceLoader<InjectablesProvider> providers = AccessController.doPrivileged(new LoadProviders());
        final Iterator<InjectablesProvider> iterator = providers.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        } else {
            throw new IllegalStateException(Messages.format("CHAINLINK-000000.injectables.provider.unavailable"));
        }
    }
}
