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

import io.machinecode.chainlink.spi.inject.Injectables;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InjectablesProviderImpl implements InjectablesProvider {

    private static final Logger log = Logger.getLogger(InjectablesProviderImpl.class);

    private static final ThreadLocal<Injectables> injectables = new ThreadLocal<>();

    @Override
    public void setInjectables(final Injectables injectables) {
        InjectablesProviderImpl.injectables.set(injectables);
    }

    @Override
    public Injectables getInjectables() {
        return injectables.get();
    }
}
