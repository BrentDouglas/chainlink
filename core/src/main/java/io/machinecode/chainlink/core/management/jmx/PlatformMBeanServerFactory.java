package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class PlatformMBeanServerFactory implements MBeanServerFactory {

    @Override
    public MBeanServer produce(final LoaderConfiguration configuration) throws Exception {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
