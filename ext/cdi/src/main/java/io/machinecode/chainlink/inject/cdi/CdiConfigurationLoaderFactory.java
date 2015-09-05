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
package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationLoaderFactory;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiConfigurationLoaderFactory implements ConfigurationLoaderFactory, Extension {

    private final BeanManager beanManager;

    public CdiConfigurationLoaderFactory(final BeanManager manager) {
        beanManager = manager;
    }

    public CdiConfigurationLoaderFactory() {
        this(CdiExtension.beanManager);
    }

    @Override
    public ConfigurationLoader produce(final Dependencies dependencies, final PropertyLookup properties) throws Exception {
        return new CdiConfigurationLoader(beanManager);
    }
}
