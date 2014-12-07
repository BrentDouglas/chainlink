package io.machinecode.chainlink.ee.wildfly.schema;

import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.configuration.Config;
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

    public static final JobOperatorAdd INSTANCE = new JobOperatorAdd();

    @Override
    protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
        final PathAddress address = PathAddress.pathAddress(operation.get(OP_ADDR));
        final String name = address.getLastElement().getValue();
        context.addStep(new AbstractDeploymentChainStep() {
            public void execute(final DeploymentProcessorTarget processorTarget) {
                final Config config = new Config(
                        nodeToString(model.get(WildFlyConstants.CLASS_LOADER_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.WORKER_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.EXECUTOR_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.REGISTRY_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.MARSHALLING_PROVIDER_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.EXECUTION_REPOSITORY_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.TRANSACTION_MANAGER_FACTORY)),
                        nodeToString(model.get(WildFlyConstants.MBEAN_SERVER_FACTORY)),
                        nodeToArray(model.get(WildFlyConstants.JOB_LOADER_FACTORY)),
                        nodeToArray(model.get(WildFlyConstants.ARTIFACT_LOADER_FACTORY)),
                        nodeToArray(model.get(WildFlyConstants.INJECTOR_FACTORY)),
                        nodeToArray(model.get(WildFlyConstants.SECURITY_CHECK_FACTORY))
                );
                //TODO Properties
                processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.POST_MODULE, Phase.POST_MODULE_BATCH_ENVIRONMENT, new JobOperatorProcessor(name, config));
            }
        }, OperationContext.Stage.RUNTIME);
    }

    String nodeToString(final ModelNode node) {
        return node.isDefined()
                ? node.asString()
                : null;
    }

    String[] nodeToArray(final ModelNode root) {
        if (!root.isDefined()) {
            return null;
        }
        final List<ModelNode> nodes = root.asList();
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        final String[] ret = new String[nodes.size()];
        for (int i = 0; i < nodes.size(); ++i) {
            ret[i] = nodeToString(nodes.get(i));
        }
        return ret;
    }
}
