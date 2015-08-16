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
package io.machinecode.chainlink.core.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.jsl.xml.XmlJob;
import io.machinecode.chainlink.spi.Messages;

import javax.batch.operations.NoSuchJobException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class XmlJobLoader extends AbstractJobLoader {

    private final Unmarshaller unmarshaller;
    private final ClassLoader loader;

    public XmlJobLoader(final ClassLoader loader) throws JAXBException {
        this.loader = loader;
        final JAXBContext context = JAXBContext.newInstance(XmlJob.class);
        unmarshaller = context.createUnmarshaller();
    }

    final TMap<String, Node> repos = new THashMap<>();

    public abstract String getPrefix();

    @Override
    protected Node doLoad(final String jslName) {
        final Node cached = repos.get(jslName);
        if (cached != null) {
            return cached;
        }
        final InputStream stream = loader.getResourceAsStream(this.getPrefix() + jslName + ".xml");
        if (stream == null) {
            throw new NoSuchJobException(Messages.format("CHAINLINK-003000.job.loader.no.file", this.getPrefix() + jslName + ".xml"));
        }

        final XmlJob job;
        try {
            job = (XmlJob) unmarshaller.unmarshal(stream);
        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                //
            }
        }
        final Node node = new Node(jslName, job);
        repos.put(jslName, node);
        return node;
    }
}
