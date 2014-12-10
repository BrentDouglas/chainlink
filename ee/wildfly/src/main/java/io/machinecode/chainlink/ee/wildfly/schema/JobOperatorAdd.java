package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.processor.JobOperatorProcessor;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

import java.util.List;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class JobOperatorAdd extends AbstractAddStepHandler {

    public static final JobOperatorAdd GLOBAL_INSTANCE = new JobOperatorAdd(true);
    public static final JobOperatorAdd DEPLOYMENT_INSTANCE = new JobOperatorAdd(false);

    private final boolean global;

    public JobOperatorAdd(final boolean global) {
        this.global = global;
    }

    @Override
    protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
        final PathAddress address = PathAddress.pathAddress(operation.get(OP_ADDR));
        final String name = address.getLastElement().getValue();
        context.addStep(new DeployJobOperator(global, name, model), OperationContext.Stage.RUNTIME);
    }

    public static class DeployJobOperator extends AbstractDeploymentChainStep {

        private final boolean global;
        final String name;
        final ModelNode model;

        public DeployJobOperator(final boolean global, final String name, final ModelNode model) {
            this.global = global;
            this.name = name;
            this.model = model;
        }

        @Override
        public void execute(final DeploymentProcessorTarget processorTarget) {
            processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.POST_MODULE, Phase.POST_MODULE_BATCH_ENVIRONMENT, new JobOperatorProcessor(global, name, model));
        }
    }
}
