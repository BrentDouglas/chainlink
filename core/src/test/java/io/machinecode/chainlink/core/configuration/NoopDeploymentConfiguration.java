package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;
import io.machinecode.chainlink.spi.configuration.DeploymentModel;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.configuration.SubSystemModel;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoopDeploymentConfiguration implements DeploymentConfiguration {
    @Override
    public void configureDeployment(final DeploymentModel model) throws Exception {
        //no op
    }
}
