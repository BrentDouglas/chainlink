package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.core.security.SecurityCheckImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.security.SecurityCheck;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RuntimeConfigurationImpl extends ConfigurationImpl implements RuntimeConfiguration {

    private final JobLoader jobLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;
    private final SecurityCheck securityCheck;

    public RuntimeConfigurationImpl(final Configuration configuration) {
        super(configuration);
        this.jobLoader = new JobLoaderImpl(configuration);
        this.artifactLoader = new ArtifactLoaderImpl(configuration);
        this.injector = new InjectorImpl(configuration);
        this.securityCheck = new SecurityCheckImpl(configuration);
    }

    @Override
    public JobLoader getJobLoader() {
        return this.jobLoader;
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return this.artifactLoader;
    }

    @Override
    public Injector getInjector() {
        return this.injector;
    }

    @Override
    public SecurityCheck getSecurityCheck() {
        return this.securityCheck;
    }
}
