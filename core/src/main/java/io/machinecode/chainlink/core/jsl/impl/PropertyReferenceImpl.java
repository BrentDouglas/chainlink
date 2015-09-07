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
package io.machinecode.chainlink.core.jsl.impl;

import io.machinecode.chainlink.core.inject.InjectablesImpl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactReference;
import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.PropertyReference;
import io.machinecode.chainlink.spi.registry.Registry;

import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyReferenceImpl<T> implements PropertyReference, Serializable {
    private static final long serialVersionUID = 1L;

    protected final PropertiesImpl properties;
    protected final ArtifactReference ref;

    public PropertyReferenceImpl(final ArtifactReference ref, final PropertiesImpl properties) {
        this.ref = ref;
        this.properties = properties;
    }

    @Override
    public String getRef() {
        return this.ref.ref();
    }

    @Override
    public PropertiesImpl getProperties() {
        return this.properties;
    }

    public synchronized T load(final Class<T> clazz, final Configuration configuration, final ExecutionContext context) throws Exception {
        return load(this.ref, clazz, configuration, context);
    }

    public static synchronized <T> T load(final ArtifactReference ref, final Class<T> clazz, final Configuration configuration, final ExecutionContext context) throws Exception {
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final Registry registry = configuration.getRegistry();
        return registry.getOrCreateScope(context).getArtifact(clazz, ref.ref(), new Callable<T>() {
            @Override
            public T call() throws Exception {
                final T that = ref.load(clazz, injectionContext, context);
                if (that == null) {
                    throw new IllegalStateException(Messages.format("CHAINLINK-025004.artifact.null", context, ref.ref()));
                }
                return that;
            }
        });
    }

    private transient Injectables _injectables;

    protected synchronized Injectables _injectables(final Configuration configuration, final ExecutionContext context) {
        if (this._injectables == null) {
            this._injectables = new InjectablesImpl(
                    context.getJobContext(),
                    context.getStepContext(),
                    properties.getProperties(),
                    configuration.getRegistry().getOrCreateScope(context)
            );
        }
        return this._injectables;
    }
}
