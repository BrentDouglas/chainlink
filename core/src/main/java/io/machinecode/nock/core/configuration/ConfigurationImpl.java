package io.machinecode.nock.core.configuration;

import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ConfigurationImpl implements Configuration {

    private static final JobLoader[] JOB_LOADERS = new JobLoader[0];
    private static final ArtifactLoader[] ARTIFACT_LOADERS = new ArtifactLoader[0];

    private final ClassLoader loader;
    private final Repository repository;
    private final TransactionManager transactionManager;
    private final JobLoader[] jobLoaders;
    private final ArtifactLoader[] artifactLoaders;

    public ConfigurationImpl(final ClassLoader loader) {
        this.loader = loader;
        this.repository = new LocalRepository();
        this.transactionManager = new LocalTransactionManager(180); //TODO Should not be here
        this.jobLoaders = JOB_LOADERS;
        this.artifactLoaders = ARTIFACT_LOADERS;
    }

    public ConfigurationImpl(final Configuration configuration) {
        this.loader = configuration.getClassLoader();
        this.repository = configuration.getRepository();
        this.transactionManager = configuration.getTransactionManager();
        this.jobLoaders = configuration.getJobLoaders();
        this.artifactLoaders = configuration.getArtifactLoaders();
    }

    public ConfigurationImpl(final Builder builder) {
        this.loader = builder.loader;
        this.repository = builder.repository;
        this.transactionManager = builder.transactionManager;
        this.jobLoaders = builder.jobLoaders;
        this.artifactLoaders = builder.artifactLoaders;
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.loader;
    }

    @Override
    public Repository getRepository() {
        return this.repository;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    @Override
    public JobLoader[] getJobLoaders() {
        return this.jobLoaders;
    }

    @Override
    public ArtifactLoader[] getArtifactLoaders() {
        return this.artifactLoaders;
    }


    public static class Builder {
        private ClassLoader loader;
        private Repository repository;
        private TransactionManager transactionManager;
        private JobLoader[] jobLoaders;
        private ArtifactLoader[] artifactLoaders;

        public Builder setLoader(final ClassLoader loader) {
            this.loader = loader;
            return this;
        }

        public Builder setRepository(final Repository repository) {
            this.repository = repository;
            return this;
        }

        public Builder setTransactionManager(final TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
            return this;
        }

        public Builder setJobLoaders(final JobLoader[] jobLoaders) {
            this.jobLoaders = jobLoaders;
            return this;
        }

        public Builder setArtifactLoaders(final ArtifactLoader[] artifactLoaders) {
            this.artifactLoaders = artifactLoaders;
            return this;
        }

        public ConfigurationImpl build() {
            return new ConfigurationImpl(this);
        }
    }
}
