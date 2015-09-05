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

import io.machinecode.chainlink.rt.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.dmr.ModelType;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyDefinition extends PersistentResourceDefinition {

    public static final PropertyDefinition INSTANCE = new PropertyDefinition();

    public PropertyDefinition() {
        super(
                PathElement.pathElement(WildFlyConstants.PROPERTY),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.JOB_OPERATOR, WildFlyConstants.PROPERTY),
                NoopAddHandler.INSTANCE,
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
    }

    protected static final SimpleAttributeDefinition NAME = new SimpleAttributeDefinitionBuilder(WildFlyConstants.NAME, ModelType.STRING)
            .setAllowNull(false)
            .build();
    protected static final SimpleAttributeDefinition VALUE = new SimpleAttributeDefinitionBuilder(WildFlyConstants.VALUE, ModelType.STRING)
            .setAllowNull(false)
            .setAllowExpression(true)
            .build();

    static final SimpleAttributeDefinition[] ATTRIBUTES = {
            NAME,
            VALUE
    };

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.<AttributeDefinition>asList(ATTRIBUTES);
    }
}
