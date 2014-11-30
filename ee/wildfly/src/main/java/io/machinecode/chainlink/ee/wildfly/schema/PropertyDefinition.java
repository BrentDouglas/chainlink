package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PropertyDefinition extends PersistentResourceDefinition {

    public static final PropertyDefinition INSTANCE = new PropertyDefinition();

    public PropertyDefinition() {
        super(
                PathElement.pathElement(WildFlyConstants.PROPERTY),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.PROPERTY),
                new PropertyAdd(),
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

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.<AttributeDefinition>asList(NAME, VALUE);
    }

    public static class PropertyAdd extends AbstractAddStepHandler {
        @Override
        protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
            //
        }
    }
}
