package io.machinecode.nock.core.configuration;

import io.machinecode.nock.core.loader.ArtifactLoaderImpl;
import io.machinecode.nock.core.loader.JobLoaderImpl;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RuntimeConfigurationImpl extends ConfigurationImpl implements RuntimeConfiguration {

    private final JobLoader jobLoader;
    private final ArtifactLoader artifactLoader;

    public RuntimeConfigurationImpl(final Configuration configuration) {
        super(configuration);
        this.jobLoader = new JobLoaderImpl(configuration);
        this.artifactLoader = new ArtifactLoaderImpl(configuration);
    }

    @Override
    public JobLoader getJobLoader() {
        return this.jobLoader;
    }

    @Override
    public ArtifactLoader getArtifactLoader() {
        return this.artifactLoader;
    }
}
