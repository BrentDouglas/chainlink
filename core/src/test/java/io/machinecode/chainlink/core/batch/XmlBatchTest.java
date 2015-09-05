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
package io.machinecode.chainlink.core.batch;

import io.machinecode.chainlink.core.inject.batch.BatchArtifacts;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class XmlBatchTest {

    private static ClassLoader classLoader = XmlBatchTest.class.getClassLoader();

    private static final Unmarshaller unmarshaller;

    static {
        final JAXBContext context;
        try {
            context = JAXBContext.newInstance(BatchArtifacts.class);
            unmarshaller = context.createUnmarshaller();
        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static BatchArtifacts read(final InputStream stream) throws JAXBException {
        final BatchArtifacts artifacts;
        try {
            artifacts = (BatchArtifacts) unmarshaller.unmarshal(stream);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                //
            }
        }
        return artifacts;
    }

    @Test
    public void batchConfigTest() throws JAXBException {
        final BatchArtifacts that = read(classLoader.getResourceAsStream("batch/batch-1.xml"));

        Assert.assertEquals(2, that.getRefs().size());

        Assert.assertEquals("id1", that.getRefs().get(0).getId());
        Assert.assertEquals("java.lang.Object", that.getRefs().get(0).getClazz());

        Assert.assertEquals("id2", that.getRefs().get(1).getId());
        Assert.assertEquals("java.lang.String", that.getRefs().get(1).getClazz());
    }
}
