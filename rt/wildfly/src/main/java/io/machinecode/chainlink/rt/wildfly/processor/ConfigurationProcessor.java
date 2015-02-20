package io.machinecode.chainlink.rt.wildfly.processor;

import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.modules.Module;
import org.jboss.modules.ref.WeakReference;
import org.jboss.msc.value.InjectedValue;
import org.jboss.msc.value.ReferenceValue;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationProcessor implements DeploymentUnitProcessor {

    final InjectedValue<ClassLoader> loader;

    public ConfigurationProcessor(final InjectedValue<ClassLoader> loader) {
        this.loader = loader;
    }

    @Override
    public void deploy(final DeploymentPhaseContext deploymentPhaseContext) throws DeploymentUnitProcessingException {
        final DeploymentUnit deploymentUnit = deploymentPhaseContext.getDeploymentUnit();
        final Module module = deploymentUnit.getAttachment(Attachments.MODULE);
        if (module == null) {
            throw new DeploymentUnitProcessingException(getClass().getName() + " run in wrong phase."); //TODO Message
        }
        final ClassLoader loader = module.getClassLoader();
        this.loader.setValue(new ReferenceValue<>(new WeakReference<>(loader)));
    }

    @Override
    public void undeploy(final DeploymentUnit deploymentUnit) {
        // no op
    }
}
