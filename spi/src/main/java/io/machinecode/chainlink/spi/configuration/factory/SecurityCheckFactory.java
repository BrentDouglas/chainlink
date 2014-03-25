package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.security.SecurityCheck;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface SecurityCheckFactory extends Factory<SecurityCheck, LoaderConfiguration> {

}
