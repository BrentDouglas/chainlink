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

import io.machinecode.chainlink.rt.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.StringListAttributeDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.machinecode.chainlink.rt.wildfly.schema.Attributes.list;
import static io.machinecode.chainlink.rt.wildfly.schema.Attributes.string;
import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkDefinition extends PersistentResourceDefinition {

    public static final ChainlinkDefinition INSTANCE = new ChainlinkDefinition();

    public ChainlinkDefinition() {
        super(
                PathElement.pathElement(SUBSYSTEM, WildFlyConstants.SUBSYSTEM_NAME),
                ChainlinkExtension.getResourceDescriptionResolver(),
                ChainlinkAdd.INSTANCE,
                ChainlinkRemove.INSTANCE
        );
    }


    protected static final SimpleAttributeDefinition REF = string(WildFlyConstants.REF);
    protected static final StringListAttributeDefinition CONFIGURATION_LOADERS = list(WildFlyConstants.CONFIGURATION_LOADERS);

    static final AttributeDefinition[] ATTRIBUTES = {
            REF,
            CONFIGURATION_LOADERS
    };

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.asList(ATTRIBUTES);
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                JobOperatorDefinition.GLOBAL_INSTANCE,
                DeploymentDefinition.INSTANCE
        );
    }
}
