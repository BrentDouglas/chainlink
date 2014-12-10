package io.machinecode.chainlink.ee.wildfly.schema;

import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

import java.util.List;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class DeploymentAdd extends AbstractBoottimeAddStepHandler {

    static final DeploymentAdd INSTANCE = new DeploymentAdd();

    @Override
    protected void performBoottime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
        //final ServiceTarget target = context.getServiceTarget();

    }

    /*
    public static class DependenciesStep extends AbstractDeploymentChainStep {
        @Override
        public void execute(DeploymentProcessorTarget processorTarget) {
            //processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.DEPENDENCIES, Phase.DEPENDENCIES_BATCH, ChainlinkDependencyProcessor.INSTANCE);
        }
    }
    */
}
