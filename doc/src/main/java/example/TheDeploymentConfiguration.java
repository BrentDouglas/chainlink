package example;

import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;
import io.machinecode.chainlink.spi.configuration.DeploymentModel;

import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Named
public class TheDeploymentConfiguration implements DeploymentConfiguration {

    @Override
    public void configureDeployment(final DeploymentModel model) throws Exception {
        model.getJobOperator("other");
    }
}
