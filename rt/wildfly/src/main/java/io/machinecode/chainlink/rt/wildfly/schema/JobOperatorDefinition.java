package io.machinecode.chainlink.rt.wildfly.schema;

import io.machinecode.chainlink.rt.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import org.jboss.as.controller.AbstractAddStepHandler;
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

    private static Element declaration(final String name) {
        return new ElementBuilder(
                PathElement.pathElement(name),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.JOB_OPERATOR, name)
        ).addAttribute(new SimpleAttributeDefinitionBuilder(WildFlyConstants.REF, ModelType.STRING)
                .setAllowNull(true)
                .setAllowExpression(true)
                .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
                .build()
        ).setAddHandler(
                NoopAddHandler.INSTANCE
        ).setRemoveHandler(
                ReloadRequiredRemoveStepHandler.INSTANCE
        ).build();
    }

    protected static final SimpleAttributeDefinition REF = new SimpleAttributeDefinitionBuilder(WildFlyConstants.REF, ModelType.STRING)
            .setAllowNull(true)
            .setAllowExpression(true)
            .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
            .build();

    protected static final Element CLASS_LOADER = declaration(WildFlyConstants.CLASS_LOADER);
    protected static final Element EXECUTOR = declaration(WildFlyConstants.EXECUTOR);
    protected static final Element TRANSPORT = declaration(WildFlyConstants.TRANSPORT);
    protected static final Element REGISTRY = declaration(WildFlyConstants.REGISTRY);
    protected static final Element MARSHALLING = declaration(WildFlyConstants.MARSHALLING);
    protected static final Element EXECUTION_REPOSITORY = declaration(WildFlyConstants.REPOSITORY);
    protected static final Element TRANSACTION_MANAGER = declaration(WildFlyConstants.TRANSACTION_MANAGER);
    protected static final Element MBEAN_SERVER = declaration(WildFlyConstants.MBEAN_SERVER);
    protected static final Element JOB_LOADER = declaration(WildFlyConstants.JOB_LOADER);
    protected static final Element ARTIFACT_LOADER = declaration(WildFlyConstants.ARTIFACT_LOADER);
    protected static final Element SECURITY = declaration(WildFlyConstants.SECURITY);

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Collections.<AttributeDefinition>singletonList(REF);
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                CLASS_LOADER,
                EXECUTOR,
                TRANSPORT,
                REGISTRY,
                MARSHALLING,
                EXECUTION_REPOSITORY,
                TRANSACTION_MANAGER,
                MBEAN_SERVER,
                JOB_LOADER,
                ARTIFACT_LOADER,
                SECURITY,
                PropertyDefinition.INSTANCE
        );
    }
}
