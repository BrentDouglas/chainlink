package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.loader.JobLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface JobLoaderFactory extends Factory<JobLoader, LoaderConfiguration> {

}
