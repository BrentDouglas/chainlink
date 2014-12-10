package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.WildFlyEnvironment;
import io.machinecode.chainlink.ee.wildfly.processor.ChainlinkDependencyProcessor;
import io.machinecode.chainlink.ee.wildfly.processor.ConfigurationProcessor;
import io.machinecode.chainlink.ee.wildfly.service.ChainlinkService;
import io.machinecode.chainlink.ee.wildfly.service.ConfigurationService;
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
import org.jboss.msc.value.InjectedValue;

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
        final InjectedValue<ClassLoader> loader = new InjectedValue<>();

        target.addService(ChainlinkService.SERVICE_NAME, new ChainlinkService(environment))
                .setInitialMode(ServiceController.Mode.ON_DEMAND)
                .install();
        target.addService(ConfigurationService.SERVICE_NAME, new ConfigurationService(loader))
                .setInitialMode(ServiceController.Mode.ON_DEMAND)
                .install();

        context.addStep(new ConfigurationStep(loader), OperationContext.Stage.RUNTIME);
        context.addStep(new DependenciesStep(), OperationContext.Stage.RUNTIME);
    }

    public static class DependenciesStep extends AbstractDeploymentChainStep {
        @Override
        public void execute(final DeploymentProcessorTarget processorTarget) {
            processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.DEPENDENCIES, Phase.DEPENDENCIES_BATCH, ChainlinkDependencyProcessor.INSTANCE);
        }
    }

    public static class ConfigurationStep extends AbstractDeploymentChainStep {

        final InjectedValue<ClassLoader> loader;

        public ConfigurationStep(final InjectedValue<ClassLoader> loader) {
            this.loader = loader;
        }

        @Override
        public void execute(final DeploymentProcessorTarget processorTarget) {
            processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.POST_MODULE, Phase.POST_MODULE_BATCH_ENVIRONMENT, new ConfigurationProcessor(loader));
        }
    }
}
