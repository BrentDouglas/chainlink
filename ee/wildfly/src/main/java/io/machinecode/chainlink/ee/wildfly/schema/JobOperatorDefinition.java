package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.ee.wildfly.ChainlinkExtension;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.service.JobOperatorService;
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
public class JobOperatorDefinition extends PersistentResourceDefinition {

    public static JobOperatorDefinition INSTANCE = new JobOperatorDefinition();

    public JobOperatorDefinition() {
        super(
                PathElement.pathElement(WildFlyConstants.JOB_OPERATOR, PathElement.WILDCARD_VALUE),
                ChainlinkExtension.getResourceDescriptionResolver(WildFlyConstants.JOB_OPERATOR),
                new JobOperatorAdd(),
                ReloadRequiredRemoveStepHandler.INSTANCE
        );
    }

    protected static final SimpleAttributeDefinition ID = new SimpleAttributeDefinitionBuilder(WildFlyConstants.ID, ModelType.STRING)
            .setAllowNull(false)
            .setFlags(AttributeAccess.Flag.RESTART_RESOURCE_SERVICES)
            .build();
    protected static final RefDefinition WORKER_FACTORY = new RefDefinition(WildFlyConstants.WORKER_FACTORY, "io.machinecode.chainlink.core.execution.EventedWorkerFactory");
    protected static final RefDefinition MARSHALLER_FACTORY = new RefDefinition(WildFlyConstants.MARSHALLER_FACTORY, "io.machinecode.chainlink.marshalling.jboss.JBossMarshallerFactory");
    protected static final RefDefinition EXECUTION_REPOSITORY_FACTORY = new RefDefinition(WildFlyConstants.EXECUTION_REPOSITORY_FACTORY, "io.machinecode.chainlink.repository.memory.MemoryExecutionRepository");
    protected static final RefDefinition JOB_LOADER_FACTORY = new RefDefinition(WildFlyConstants.JOB_LOADER_FACTORY);
    protected static final RefDefinition ARTIFACT_LOADER_FACTORY = new RefDefinition(WildFlyConstants.ARTIFACT_LOADER_FACTORY);
    protected static final RefDefinition INJECTOR_FACTORY = new RefDefinition(WildFlyConstants.INJECTOR_FACTORY);
    protected static final RefDefinition SECURITY_CHECK_FACTORY = new RefDefinition(WildFlyConstants.SECURITY_CHECK_FACTORY);

    @Override
    public Collection<AttributeDefinition> getAttributes() {
        return Arrays.<AttributeDefinition>asList(
                ID
        );
    }

    @Override
    protected List<? extends PersistentResourceDefinition> getChildren() {
        return Arrays.asList(
                WORKER_FACTORY,
                MARSHALLER_FACTORY,
                EXECUTION_REPOSITORY_FACTORY,
                JOB_LOADER_FACTORY,
                ARTIFACT_LOADER_FACTORY,
                INJECTOR_FACTORY,
                SECURITY_CHECK_FACTORY
        );
    }

    public static class JobOperatorAdd extends AbstractAddStepHandler {
        @Override
        protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
            final String id = JobOperatorDefinition.ID.resolveModelAttribute(context, model).asString();
            final JobOperatorService service = new JobOperatorService(id);
            final ServiceController<ExtendedJobOperator> serviceController = context.getServiceTarget()
                    .addService(JobOperatorService.SERVICE_NAME.append(id), service)
                    .setInitialMode(ServiceController.Mode.ON_DEMAND)
                    .addListener(verificationHandler)
                    .install();
            if (newControllers != null) {
                newControllers.add(serviceController);
            }
        }
    }
}
