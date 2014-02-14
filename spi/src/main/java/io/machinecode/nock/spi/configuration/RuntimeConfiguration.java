package io.machinecode.nock.spi.configuration;

import io.machinecode.nock.spi.inject.Injector;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RuntimeConfiguration extends Configuration {

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();
}
