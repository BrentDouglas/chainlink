package example;

import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;

import javax.inject.Named;
import java.lang.management.ManagementFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Named
public class TheJobOperatorConfiguration implements JobOperatorConfiguration {

    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        model.setProperty("foo", "bar");
        model.getMBeanServer().setValue(ManagementFactory.getPlatformMBeanServer());
        //This is changing the value set in xml from 'theSecurity' to 'otherSecurity'
        model.getSecurity("security").setRef("otherSecurity");
        //Re-add 'theSecurity' after 'otherSecurity'
        model.getSecurity("secondSecurity").setRef("theSecurity");
    }
}
