/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;

import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InjectionContextImpl implements InjectionContext {

    private final WeakReference<ClassLoader> classLoader;
    private final ArtifactLoader artifactLoader;
    private final InjectablesProvider provider;

    public InjectionContextImpl(final ClassLoader classLoader, final ArtifactLoader artifactLoader) {
        this.classLoader = new WeakReference<>(classLoader);
        this.artifactLoader = artifactLoader;
        this.provider = new InjectablesProviderImpl();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader.get();
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return this.artifactLoader;
    }

    @Override
    public InjectablesProvider getProvider() {
        return provider;
    }
}
