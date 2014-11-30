package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;

import javax.management.MBeanServer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface MBeanServerFactory extends Factory<MBeanServer, LoaderConfiguration> {

}
