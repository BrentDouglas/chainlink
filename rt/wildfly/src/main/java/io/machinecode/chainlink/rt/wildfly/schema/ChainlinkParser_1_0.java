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
package io.machinecode.chainlink.rt.wildfly.schema;

import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.PersistentResourceXMLDescription;
import org.jboss.dmr.ModelNode;
import org.jboss.staxmapper.XMLElementReader;
import org.jboss.staxmapper.XMLExtendedStreamReader;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.List;

import static org.jboss.as.controller.PersistentResourceXMLDescription.PersistentResourceXMLBuilder;
import static org.jboss.as.controller.PersistentResourceXMLDescription.builder;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkParser_1_0 implements XMLElementReader<List<ModelNode>>, XMLStreamConstants {

    public static final ChainlinkParser_1_0 INSTANCE = new ChainlinkParser_1_0();

    private static final PersistentResourceXMLDescription xmlDescription;

    static {
        xmlDescription = builder(ChainlinkDefinition.INSTANCE)
                .addAttributes(ChainlinkDefinition.ATTRIBUTES)
                .addChild(jobOperator(JobOperatorDefinition.GLOBAL_INSTANCE))
                .addChild(builder(DeploymentDefinition.INSTANCE)
                        .addAttributes(DeploymentDefinition.ATTRIBUTES)
                        .addChild(jobOperator(JobOperatorDefinition.DEPLOYMENT_INSTANCE))
                ).build();
    }

    private static PersistentResourceXMLBuilder jobOperator(final JobOperatorDefinition definition) {
        return builder(definition)
                .addAttributes(JobOperatorDefinition.ATTRIBUTES)
                .addChild(builder(PropertyDefinition.INSTANCE)
                        .addAttributes(PropertyDefinition.ATTRIBUTES)
                );
    }

    @Override
    public void readElement(final XMLExtendedStreamReader reader, final List<ModelNode> operations) throws XMLStreamException {
        xmlDescription.parse(reader, PathAddress.EMPTY_ADDRESS, operations);
    }
}
