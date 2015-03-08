package io.machinecode.chainlink.rt.wildfly.schema;

import io.machinecode.chainlink.rt.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
import org.jboss.as.controller.SimpleAttributeDefinition;
import org.jboss.as.controller.SimpleAttributeDefinitionBuilder;
import org.jboss.as.controller.registry.AttributeAccess;
import org.jboss.dmr.ModelType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeploymentDefinition extends PersistentResourceDefinition {

    public static final DeploymentDefinition INSTANCE = new DeploymentDefinition();

    public DeploymentDefinition() {
        super(
                PathElement.pathElement(WildFlyConstants.DEPLOYMENT),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.DEPLOYMENT),
                NoopAddHandler.INSTANCE,
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
    }

    protected static final SimpleAttributeDefinition REF = new SimpleAttributeDefinitionBuilder(WildFlyConstants.REF, ModelType.STRING)
            .setAllowNull(true)
            .setAllowExpression(true)
            .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
            .build();

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.<AttributeDefinition>singletonList(REF);
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                JobOperatorDefinition.DEPLOYMENT_INSTANCE
        );
    }
}
