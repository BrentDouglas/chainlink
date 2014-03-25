package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.loader.JobLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobLoaderFactory extends Factory<JobLoader, LoaderConfiguration> {

}
