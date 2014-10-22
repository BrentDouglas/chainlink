package io.machinecode.chainlink.ee.wildfly;

import io.machinecode.chainlink.ee.wildfly.schema.ChainlinkDefinition;
import io.machinecode.chainlink.ee.wildfly.schema.ChainlinkParser_1_0;
import org.jboss.as.controller.Extension;
import org.jboss.as.controller.ExtensionContext;
import org.jboss.as.controller.SubsystemRegistration;
import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;
import org.jboss.as.controller.parsing.ExtensionParsingContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkExtension implements Extension, WildFlyConstants {

    public static ResourceDescriptionResolver getResourceDescriptionResolver(final String... keyPrefix) {
        final StringBuilder builder = new StringBuilder(SUBSYSTEM_NAME);
        for (final String prefix : keyPrefix) {
            builder.append('.').append(prefix);
        }
        return new StandardResourceDescriptionResolver(builder.toString(), RESOURCE_NAME, ChainlinkExtension.class.getClassLoader(), true, true);
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
