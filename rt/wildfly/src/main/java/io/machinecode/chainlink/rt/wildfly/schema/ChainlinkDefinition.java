package io.machinecode.chainlink.rt.wildfly.schema;

import io.machinecode.chainlink.rt.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.SUBSYSTEM;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkDefinition extends PersistentResourceDefinition {

    public static final ChainlinkDefinition INSTANCE = new ChainlinkDefinition();

    public ChainlinkDefinition() {
        super(
                PathElement.pathElement(SUBSYSTEM, WildFlyConstants.SUBSYSTEM_NAME),
                ChainlinkExtension.getResourceDescriptionResolver(),
                ChainlinkAdd.INSTANCE,
                ChainlinkRemove.INSTANCE
        );
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                JobOperatorDefinition.GLOBAL_INSTANCE,
                DeploymentDefinition.INSTANCE
        );
    }

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.emptyList();
    }

}
