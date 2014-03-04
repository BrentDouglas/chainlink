package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.inject.InjectorImpl;
import io.machinecode.chainlink.core.loader.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.loader.JobLoaderImpl;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.RuntimeConfiguration;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RuntimeConfigurationImpl extends ConfigurationImpl implements RuntimeConfiguration {

    private final JobLoader jobLoader;
    private final ArtifactLoader artifactLoader;
    private final Injector injector;

    public RuntimeConfigurationImpl(final Configuration configuration) {
        super(configuration);
        this.jobLoader = new JobLoaderImpl(configuration);
        this.artifactLoader = new ArtifactLoaderImpl(configuration);
        this.injector = new InjectorImpl(configuration);
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
}
