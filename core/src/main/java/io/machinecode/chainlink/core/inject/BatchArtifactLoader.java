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

import io.machinecode.chainlink.core.inject.batch.BatchArtifactRef;
import io.machinecode.chainlink.core.inject.batch.BatchArtifacts;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(BatchArtifactLoader.class);

    private final Unmarshaller unmarshaller;
    private final List<BatchArtifactRef> artifacts;

    private final InjectablesProvider provider;

    public BatchArtifactLoader(final String prefix, final ClassLoader loader, final InjectablesProvider provider) throws JAXBException, IOException {
        this.provider = provider;
        final JAXBContext context = JAXBContext.newInstance(BatchArtifacts.class);
        unmarshaller = context.createUnmarshaller();
        final BatchArtifacts batchArtifacts;
        final InputStream stream = loader.getResourceAsStream(prefix + "batch.xml");
        if (stream == null) {
            this.artifacts = Collections.emptyList();
            return;
        }
        try {
            batchArtifacts = (BatchArtifacts) unmarshaller.unmarshal(stream);
        } finally {
            stream.close();
        }
        this.artifacts = batchArtifacts.getRefs();
        for (final BatchArtifactRef ref : artifacts) {
            if (ref.getId() == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-025005.artifact.batch.file.invalid", "id"));
            }
            if (ref.getClazz() == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-025005.artifact.batch.file.invalid", "class"));
            }
        }
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        String fqcn = null;
        for (final BatchArtifactRef ref : artifacts) {
            if (ref.getId().equals(id)) {
                fqcn = ref.getClazz();
            }
        }
        if (fqcn == null) {
             return null;
        }
        final Class<?> clazz;
        try {
            clazz = loader.loadClass(fqcn);
        } catch (final ClassNotFoundException e) {
            log.errorf(e, Messages.get("CHAINLINK-002200.validation.cant.load.matching.artifact"), id, fqcn);
            return null;
        }

        final Object that;
        try {
            that = clazz.newInstance();
        } catch (final Exception e) {
            log.errorf(e, Messages.get("CHAINLINK-002200.validation.cant.load.matching.artifact"), id, fqcn);
            return null;
        }
        if (!as.isAssignableFrom(that.getClass())) {
            throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getSimpleName()));
        }
        final T bean = as.cast(that);
        Injector.inject(provider, bean);
        return bean;
    }
}
