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
package io.machinecode.chainlink.inject.jndi;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JndiArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(JndiArtifactLoader.class);

    private final InjectablesProvider provider;

    public JndiArtifactLoader() {
        this.provider = ArtifactLoaderImpl.loadProvider();
    }


    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        final T bean;
        try {
            final Object that = InitialContext.doLookup(id);
            if (as.isAssignableFrom(that.getClass())) {
                bean = as.cast(that);
            } else {
                throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getSimpleName()));
            }
        } catch (final NamingException e) {
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
        Injector.inject(provider, bean);
        return bean;
    }
}
