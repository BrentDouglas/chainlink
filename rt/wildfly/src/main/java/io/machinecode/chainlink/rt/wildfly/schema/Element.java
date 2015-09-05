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

import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.registry.OperationEntry;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class Element extends PersistentResourceDefinition {

    private final AttributeDefinition[] attributes;
    private final PersistentResourceDefinition[] children;

    public Element(final PathElement pathElement, final ResourceDescriptionResolver descriptionResolver, final OperationStepHandler addHandler,
                   final OperationStepHandler removeHandler, final OperationEntry.Flag addRestartLevel, final OperationEntry.Flag removeRestartLevel,
                   final AttributeDefinition[] attributes, final PersistentResourceDefinition[] children) {
        super(pathElement, descriptionResolver, addHandler, removeHandler, addRestartLevel, removeRestartLevel);
        this.attributes = attributes;
        this.children = children;
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.asList(attributes);
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(children);
    }

    public AttributeDefinition[] getRawAttributes() {
        return attributes;
    }

    public PersistentResourceDefinition[] getRawChildren() {
        return children;
    }
}
