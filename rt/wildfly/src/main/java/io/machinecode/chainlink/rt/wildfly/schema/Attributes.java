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
