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

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ClassLoaderArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(ClassLoaderArtifactLoader.class);

    private final InjectablesProvider provider;

    public ClassLoaderArtifactLoader(final InjectablesProvider provider) {
        this.provider = provider;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        final T bean;
        try {
            final Class<?> that = loader.loadClass(id);
            if (as.isAssignableFrom(that)) {
                bean = as.cast(that.newInstance());
            } else {
                throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getCanonicalName()));
            }
        } catch (final ClassNotFoundException e) {
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        } catch (final InstantiationException e) {
            log.warnf(Messages.get("CHAINLINK-025002.artifact.loader.instantiation"), id, as.getSimpleName());
            return null;
        } catch (final IllegalAccessException e) {
            log.warnf(Messages.get("CHAINLINK-025003.artifact.loader.access"), id, as.getSimpleName());
            return null;
        }
        Injector.inject(provider.getInjectables(), bean);
        return bean;
    }
}
