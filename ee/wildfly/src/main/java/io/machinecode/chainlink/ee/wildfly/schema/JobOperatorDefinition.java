package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import org.jboss.as.controller.AttributeDefinition;
import org.jboss.as.controller.PathElement;
import org.jboss.as.controller.PersistentResourceDefinition;
import org.jboss.as.controller.ReloadRequiredRemoveStepHandler;
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
public class JobOperatorDefinition extends PersistentResourceDefinition {

    public static JobOperatorDefinition INSTANCE = new JobOperatorDefinition();

    public JobOperatorDefinition() {
        super(
                PathElement.pathElement(WildFlyConstants.JOB_OPERATOR),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.JOB_OPERATOR),
                JobOperatorAdd.INSTANCE,
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
    }

    private static Element owned(final String name) {
        return new ElementBuilder(
                PathElement.pathElement(name),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.JOB_OPERATOR, name)
        ).addAttribute(new SimpleAttributeDefinitionBuilder(WildFlyConstants.CLASS, ModelType.STRING)
                .setAllowNull(false)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .build()
        /*
        ).addAttribute(new SimpleAttributeDefinitionBuilder(WildFlyConstants.FACTORY, ModelType.STRING)
                .setAllowNull(true)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .build()
        ).addAttribute(new SimpleAttributeDefinitionBuilder(WildFlyConstants.REF, ModelType.STRING)
                .setAllowNull(true)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .build()
        ).addAttribute(new SimpleAttributeDefinitionBuilder(WildFlyConstants.JNDI_NAME, ModelType.STRING)
                .setAllowNull(true)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .build()
        */
        ).setAddHandler(
                NoopAddHandler.INSTANCE
        ).setRemoveHandler(
                ReloadRequiredRemoveStepHandler.INSTANCE
        ).build();
    }

    protected static final Element CLASS_LOADER_FACTORY = owned(WildFlyConstants.CLASS_LOADER_FACTORY);
    protected static final Element WORKER_FACTORY = owned(WildFlyConstants.WORKER_FACTORY);
    protected static final Element EXECUTOR_FACTORY = owned(WildFlyConstants.EXECUTOR_FACTORY);
    protected static final Element REGISTRY_FACTORY = owned(WildFlyConstants.REGISTRY_FACTORY);
    protected static final Element MARSHALLING_PROVIDER_FACTORY = owned(WildFlyConstants.MARSHALLING_PROVIDER_FACTORY);
    protected static final Element EXECUTION_REPOSITORY_FACTORY = owned(WildFlyConstants.EXECUTION_REPOSITORY_FACTORY);
    protected static final Element TRANSACTION_MANAGER_FACTORY = owned(WildFlyConstants.TRANSACTION_MANAGER_FACTORY);
    protected static final Element MBEAN_SERVER_FACTORY = owned(WildFlyConstants.MBEAN_SERVER_FACTORY);
    protected static final Element JOB_LOADER_FACTORY = owned(WildFlyConstants.JOB_LOADER_FACTORY);
    protected static final Element ARTIFACT_LOADER_FACTORY = owned(WildFlyConstants.ARTIFACT_LOADER_FACTORY);
    protected static final Element INJECTOR_FACTORY = owned(WildFlyConstants.INJECTOR_FACTORY);
    protected static final Element SECURITY_CHECK_FACTORY = owned(WildFlyConstants.SECURITY_CHECK_FACTORY);

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.emptyList();
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                CLASS_LOADER_FACTORY,
                WORKER_FACTORY,
                EXECUTOR_FACTORY,
                REGISTRY_FACTORY,
                MARSHALLING_PROVIDER_FACTORY,
                EXECUTION_REPOSITORY_FACTORY,
                TRANSACTION_MANAGER_FACTORY,
                MBEAN_SERVER_FACTORY,
                JOB_LOADER_FACTORY,
                ARTIFACT_LOADER_FACTORY,
                INJECTOR_FACTORY,
                SECURITY_CHECK_FACTORY,
                PropertyDefinition.INSTANCE
        );
    }
}
