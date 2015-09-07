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


import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import io.machinecode.chainlink.spi.inject.InjectionContext;
import io.machinecode.chainlink.spi.jsl.Listeners;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ListenersImpl implements Listeners, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(ListenersImpl.class);

    private final List<ListenerImpl> listeners;

    public ListenersImpl(final List<ListenerImpl> listeners) {
        if (listeners == null) {
            this.listeners = Collections.emptyList();
        } else {
            this.listeners = listeners;
        }
    }

    @Override
    public List<ListenerImpl> getListeners() {
        return this.listeners;
    }

    public List<ListenerImpl> getListenersImplementing(final Configuration configuration, final ExecutionContext context, final Class<?> clazz) throws Exception {
        final List<ListenerImpl> ret = new ArrayList<>(this.listeners.size());
        final InjectionContext injectionContext = configuration.getInjectionContext();
        final InjectablesProvider provider = injectionContext.getProvider();
        try {
            for (final ListenerImpl listener : this.listeners) {
                provider.setInjectables(listener._injectables(configuration, context));

                final Object that;
                try {
                    that = listener.load(clazz, injectionContext, context);
                } catch (final ArtifactOfWrongTypeException e) {
                    continue;
                }
                if (that == null) {
                    continue;
                }
                if (clazz.isAssignableFrom(that.getClass())) {
                    ret.add(listener);
                }
            }
        } finally {
            provider.releaseInjectables();
        }
        return ret;
    }
}
