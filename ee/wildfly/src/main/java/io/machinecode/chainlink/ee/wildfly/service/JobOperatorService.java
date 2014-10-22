package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.ee.wildfly.configuration.WildflyConfigurationDefaults;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.configuration.WildFlyConfiguration;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorService implements Service<ExtendedJobOperator> {

    private static final Logger log = Logger.getLogger(JobOperatorService.class);

    public static final ServiceName SERVICE_NAME = ChainlinkService.SERVICE_NAME.append(WildFlyConstants.JOB_OPERATOR);

    public final ServiceName serviceName;
    private JobOperatorImpl operator;
    private InjectedValue<ChainlinkService> injectedChainlink = new InjectedValue<ChainlinkService>();

    public JobOperatorService(final String id) {
        serviceName = ChainlinkService.SERVICE_NAME.append(id);
    }

    @Override
    public void start(final StartContext context) throws StartException {
        try {
            operator = new JobOperatorImpl(new WildFlyConfiguration.Builder()
                    .setConfigurationDefaults(new WildflyConfigurationDefaults(
                            Thread.currentThread().getContextClassLoader(), //
                            injectedChainlink.getValue().beanManager.getValue(),
                            injectedChainlink.getValue().transactionManager.getValue())
                    )
                    //TODO
                    .build()
            );
        } catch (final Exception e) {
            throw new StartException(e);
        }
    }

    @Override
    public void stop(final StopContext context) {
        try {
            operator.close();
        } catch (final Exception e) {
            log.error("Error while stopping JobOperator", e); //TODO Message
        }
    }

    @Override
    public ExtendedJobOperator getValue() throws IllegalStateException, IllegalArgumentException {
        return operator;
    }

    public InjectedValue<ChainlinkService> getInjectedChainlink() {
        return injectedChainlink;
    }
}
