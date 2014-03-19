package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.security.SecurityCheck;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Configuration extends BaseConfiguration {

    JobLoader[] getJobLoaders();

    ArtifactLoader[] getArtifactLoaders();

    Injector[] getInjectors();

    SecurityCheck[] getSecurityChecks();
}
