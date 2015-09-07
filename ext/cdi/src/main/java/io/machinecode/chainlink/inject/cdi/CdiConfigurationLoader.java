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
import io.machinecode.chainlink.spi.inject.ClosableScope;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiConfigurationLoader implements ConfigurationLoader {

    private BeanManager beanManager;
    private ClosableScope scope;

    public CdiConfigurationLoader(final BeanManager beanManager, final ClosableScope scope) {
        this.beanManager = beanManager;
        this.scope = scope;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) throws Exception {
        return CdiArtifactLoader._inject(beanManager, as, id, scope, new NamedLiteral(id));
    }
}
