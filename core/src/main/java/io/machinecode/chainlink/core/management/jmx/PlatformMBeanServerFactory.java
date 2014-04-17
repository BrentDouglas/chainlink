package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlatformMBeanServerFactory implements MBeanServerFactory {

    @Override
    public MBeanServer produce(final LoaderConfiguration configuration) throws Exception {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
