package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.configuration.factory.MarshallingProviderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.security.SecurityCheck;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface RepositoryConfiguration extends SecurityConfiguration {

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();

    SecurityCheck getSecurityCheck();

    MarshallingProviderFactory getMarshallingProviderFactory();
}
