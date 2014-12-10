package io.machinecode.chainlink.core.management.jmx;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PlatformMBeanServerFactory implements MBeanServerFactory {

    @Override
    public MBeanServer produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return ManagementFactory.getPlatformMBeanServer();
    }
}
