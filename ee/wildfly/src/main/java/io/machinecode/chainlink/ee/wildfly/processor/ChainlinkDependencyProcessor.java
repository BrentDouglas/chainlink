package io.machinecode.chainlink.ee.wildfly.processor;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleIdentifier;
import org.jboss.modules.ModuleLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkDependencyProcessor implements DeploymentUnitProcessor {

    public static ChainlinkDependencyProcessor INSTANCE = new ChainlinkDependencyProcessor();

    private final ModuleIdentifier javax_batch_api = ModuleIdentifier.create("javax.batch.api");
    private final ModuleIdentifier io_machinecode_chainlink = ModuleIdentifier.create("io.machinecode.chainlink");

    @Override
    public void deploy(final DeploymentPhaseContext deploymentPhaseContext) throws DeploymentUnitProcessingException {
        final ModuleSpecification moduleSpecification = deploymentPhaseContext.getDeploymentUnit().getAttachment(Attachments.MODULE_SPECIFICATION);
        final ModuleLoader moduleLoader = Module.getBootModuleLoader();

        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, javax_batch_api, false, false, false, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, io_machinecode_chainlink, false, false, true, false));
    }

    @Override
    public void undeploy(final DeploymentUnit deploymentUnit) {
        // no op
    }
}
