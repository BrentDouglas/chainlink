package example;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.configuration.SubSystemModel;

import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Named
public class TheSubSystemConfiguration implements SubSystemConfiguration {
    @Override
    public void configureSubSystem(final SubSystemModel model) throws Exception {
        model.getDeployment(Constants.DEFAULT)
                .getJobOperator(Constants.DEFAULT)
                .getProperties()
                .setProperty("bar", "baz");
    }
}
