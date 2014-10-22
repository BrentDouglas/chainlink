package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.OperationStepHandler;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.dmr.ModelNode;
import org.jboss.dmr.ModelType;
import org.jboss.msc.service.ServiceController;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class RefDefinition extends PersistentResourceDefinition {

    protected final SimpleAttributeDefinition clazz;

    public RefDefinition(final String ref) {
        this(ref, null, new RefAdd());
    }

    public RefDefinition(final String ref, final String defaultValue) {
        this(ref, defaultValue, new RefAdd());
    }

    public RefDefinition(final String ref, final OperationStepHandler handler) {
        this(ref, null, handler);
    }

    public RefDefinition(final String ref, final String defaultValue, final OperationStepHandler handler) {
        super(
                PathElement.pathElement(ref),
                ChainlinkExtension.getResourceDescriptionResolver(ref),
                handler,
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
        final SimpleAttributeDefinitionBuilder clazz = new SimpleAttributeDefinitionBuilder(WildFlyConstants.CLASS, ModelType.STRING)
                .setAllowNull(false)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES);
        if (defaultValue != null) {
            clazz.setDefaultValue(new ModelNode(defaultValue));
        }
        this.clazz = clazz.build();
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.<AttributeDefinition>asList(clazz);
    }

    public static class RefAdd extends AbstractAddStepHandler {
        @Override
        protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
            //
        }
    }
}
