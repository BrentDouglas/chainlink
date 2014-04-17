package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;

import javax.management.MBeanServer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MBeanServerFactory extends Factory<MBeanServer, LoaderConfiguration> {

}
