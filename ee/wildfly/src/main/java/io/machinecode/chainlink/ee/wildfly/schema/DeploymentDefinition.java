package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;

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
                DeploymentAdd.INSTANCE,
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                JobOperatorDefinition.DEPLOYMENT_INSTANCE
        );
    }
}
