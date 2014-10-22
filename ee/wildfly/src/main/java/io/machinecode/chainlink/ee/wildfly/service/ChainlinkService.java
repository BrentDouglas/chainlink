package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.WildFlyEnvironment;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class ChainlinkService implements Service<ChainlinkService> {

    public static final ServiceName SERVICE_NAME = ServiceName.of(WildFlyConstants.SERVICE_NAME);

    final InjectedValue<TransactionManager> transactionManager = new InjectedValue<TransactionManager>();
    final InjectedValue<BeanManager> beanManager = new InjectedValue<BeanManager>();

    private WildFlyEnvironment environment;

    @Override
    public void start(final StartContext context) throws StartException {
        try {
            environment = new WildFlyEnvironment();
        } catch (Exception e) {
            throw new StartException(e);
        }
        Chainlink.setEnvironment(environment);
    }

    @Override
    public void stop(final StopContext context) {
        //TODO
    }

    @Override
    public ChainlinkService getValue() throws IllegalStateException, IllegalArgumentException {
        return this;
    }
}
