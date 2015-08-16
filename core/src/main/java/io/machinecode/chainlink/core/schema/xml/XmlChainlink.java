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
package io.machinecode.chainlink.core.schema.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlink.NAMESPACE, name = XmlChainlink.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlink extends XmlDeployment {

    public static final String ELEMENT = "chainlink";

    public static final String SCHEMA_URL = "http://machinecode.io/xml/ns/chainlink/chainlink_1_0.xsd";
    public static final String NAMESPACE = "http://machinecode.io/xml/ns/chainlink";

    public static XmlChainlink read(final InputStream stream) throws Exception {
        try {
            final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            return (XmlChainlink) unmarshaller.unmarshal(stream);
        } finally {
            stream.close();
        }
    }
}
