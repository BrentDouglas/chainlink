package example;

import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.ListModel;
import io.machinecode.chainlink.spi.security.Security;

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
        final ListModel<Security> securities = model.getSecurities();
        securities.clear();
        securities.add().setRef("otherSecurity");
        securities.add().setRef("theSecurity");
    }
}
