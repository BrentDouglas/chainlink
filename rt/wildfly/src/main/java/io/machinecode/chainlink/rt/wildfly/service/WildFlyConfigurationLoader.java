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
package io.machinecode.chainlink.rt.wildfly.service;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import javax.enterprise.inject.spi.BeanManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class WildFlyConfigurationLoader implements ConfigurationLoader {

    private final BeanManager beanManager;

    WildFlyConfigurationLoader(final BeanManager manager) {
        beanManager = manager;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        if (id.equals("io.machinecode.chainlink.inject.cdi.CdiArtifactLoaderFactory")) {
            if (!as.equals(ArtifactLoaderFactory.class)) {
                throw new ArtifactOfWrongTypeException(); //TODO
            }
            if (beanManager == null) {
                throw new IllegalStateException("Requested CDI artifact loading but no BeanManager is available."); //TODO Message
            }
            return as.cast(loader.loadClass("io.machinecode.chainlink.inject.cdi.CdiArtifactLoaderFactory").getConstructor(BeanManager.class).newInstance(beanManager));
        }
        return null;
    }
}
