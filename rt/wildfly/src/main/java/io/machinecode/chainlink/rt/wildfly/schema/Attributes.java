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
package io.machinecode.chainlink.rt.wildfly.schema;

import io.machinecode.chainlink.core.schema.xml.XmlSchema;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.AttributeParser;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.StringListAttributeDefinition;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Attributes {

    public static SimpleAttributeDefinition string(final String name) {
        return new SimpleAttributeDefinitionBuilder(name, ModelType.STRING)
                .setAllowNull(true)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .build();
    }

    public static StringListAttributeDefinition list(final String name) {
        return new StringListAttributeDefinition.Builder(name)
                .setAllowNull(true)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .setAttributeParser(XML_LIST)
                .build();
    }

    public static final AttributeParser XML_LIST = new AttributeParser() {
        @Override
        public void parseAndSetParameter(final AttributeDefinition attribute, final String value, final ModelNode operation, final XMLStreamReader reader) throws XMLStreamException {
            if (value == null) { return; }
            final ModelNode node = operation.get(attribute.getName());
            for (final String element : value.split(XmlSchema.XML_LIST_DELIMITER)) {
                node.add(parse(attribute, element, reader));
            }
        }
    };
}
