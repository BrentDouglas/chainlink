package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.WildFlyEnvironment;
import io.machinecode.chainlink.ee.wildfly.processor.ChainlinkDependencyProcessor;
import io.machinecode.chainlink.ee.wildfly.service.ChainlinkService;
import org.jboss.as.controller.AbstractBoottimeAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;

import java.util.List;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class ChainlinkAdd extends AbstractBoottimeAddStepHandler {

    static final ChainlinkAdd INSTANCE = new ChainlinkAdd();

    @Override
    protected void performBoottime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
        final ServiceTarget target = context.getServiceTarget();
        final WildFlyEnvironment environment = new WildFlyEnvironment();
        target.addService(ChainlinkService.SERVICE_NAME, new ChainlinkService(environment))
                .setInitialMode(ServiceController.Mode.ON_DEMAND)
                .install();

        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(DeploymentProcessorTarget processorTarget) {
                processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.DEPENDENCIES, Phase.DEPENDENCIES_BATCH, ChainlinkDependencyProcessor.INSTANCE);
            }
        }, OperationContext.Stage.RUNTIME);
    }
}
