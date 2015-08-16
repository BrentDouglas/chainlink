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
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.StringListAttributeDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static io.machinecode.chainlink.rt.wildfly.schema.Attributes.list;
import static io.machinecode.chainlink.rt.wildfly.schema.Attributes.string;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorDefinition extends PersistentResourceDefinition {

    public static final JobOperatorDefinition GLOBAL_INSTANCE = new JobOperatorDefinition(JobOperatorAdd.GLOBAL_INSTANCE);
    public static final JobOperatorDefinition DEPLOYMENT_INSTANCE = new JobOperatorDefinition(JobOperatorAdd.DEPLOYMENT_INSTANCE);

    public JobOperatorDefinition(final AbstractAddStepHandler add) {
        super(
                PathElement.pathElement(WildFlyConstants.JOB_OPERATOR),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.JOB_OPERATOR),
                add,
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
    }

    protected static final SimpleAttributeDefinition REF = string(WildFlyConstants.REF);
    protected static final SimpleAttributeDefinition CLASS_LOADER = string(WildFlyConstants.CLASS_LOADER);
    protected static final SimpleAttributeDefinition EXECUTOR = string(WildFlyConstants.EXECUTOR);
    protected static final SimpleAttributeDefinition TRANSPORT = string(WildFlyConstants.TRANSPORT);
    protected static final SimpleAttributeDefinition REGISTRY = string(WildFlyConstants.REGISTRY);
    protected static final SimpleAttributeDefinition MARSHALLING = string(WildFlyConstants.MARSHALLING);
    protected static final SimpleAttributeDefinition EXECUTION_REPOSITORY = string(WildFlyConstants.REPOSITORY);
    protected static final SimpleAttributeDefinition TRANSACTION_MANAGER = string(WildFlyConstants.TRANSACTION_MANAGER);
    protected static final SimpleAttributeDefinition MBEAN_SERVER = string(WildFlyConstants.MBEAN_SERVER);
    protected static final StringListAttributeDefinition JOB_LOADERS = list(WildFlyConstants.JOB_LOADERS);
    protected static final StringListAttributeDefinition ARTIFACT_LOADERS = list(WildFlyConstants.ARTIFACT_LOADERS);
    protected static final StringListAttributeDefinition SECURITIES = list(WildFlyConstants.SECURITIES);

    static final AttributeDefinition[] ATTRIBUTES = {
            REF,
            CLASS_LOADER,
            EXECUTOR,
            TRANSPORT,
            REGISTRY,
            MARSHALLING,
            EXECUTION_REPOSITORY,
            TRANSACTION_MANAGER,
            MBEAN_SERVER,
            JOB_LOADERS,
            ARTIFACT_LOADERS,
            SECURITIES
    };

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.asList(ATTRIBUTES);
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                PropertyDefinition.INSTANCE
        );
    }
}
