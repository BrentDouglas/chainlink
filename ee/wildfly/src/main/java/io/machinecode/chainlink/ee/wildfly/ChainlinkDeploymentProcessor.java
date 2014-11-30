package io.machinecode.chainlink.ee.wildfly;

import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkDeploymentProcessor implements DeploymentUnitProcessor {
    @Override
    public void deploy(final DeploymentPhaseContext deploymentPhaseContext) throws DeploymentUnitProcessingException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void undeploy(final DeploymentUnit deploymentUnit) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
