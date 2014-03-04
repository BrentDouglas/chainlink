package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RuntimeConfiguration extends Configuration {

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();
}
