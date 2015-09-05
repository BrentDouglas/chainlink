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
package io.machinecode.chainlink.rt.wildfly;

import io.machinecode.chainlink.rt.wildfly.schema.ChainlinkDefinition;
import io.machinecode.chainlink.rt.wildfly.schema.ChainlinkParser_1_0;
import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.parsing.ExtensionParsingContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkExtension implements Extension, WildFlyConstants {

    public static ResourceDescriptionResolver getResourceDescriptionResolver(final String... keyPrefix) {
        final StringBuilder builder = new StringBuilder(SUBSYSTEM_NAME);
        for (final String prefix : keyPrefix) {
            builder.append('.').append(prefix);
        }
        return new StandardResourceDescriptionResolver(builder.toString(), RESOURCE_NAME, ChainlinkExtension.class.getClassLoader(), true, false);
    }

    @Override
    public void initialize(final ExtensionContext context) {
        final SubsystemRegistration subsystem = context.registerSubsystem(
                SUBSYSTEM_NAME,
                MANAGEMENT_API_MAJOR_VERSION,
                MANAGEMENT_API_MINOR_VERSION,
                MANAGEMENT_API_MICRO_VERSION
        );
        subsystem.registerSubsystemModel(ChainlinkDefinition.INSTANCE);
    }

    @Override
    public void initializeParsers(final ExtensionParsingContext context) {
        context.setSubsystemXmlMapping(SUBSYSTEM_NAME, NAMESPACE_1_0, ChainlinkParser_1_0.INSTANCE);
    }
}
