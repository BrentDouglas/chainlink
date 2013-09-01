package io.machinecode.nock.core.configuration;

import io.machinecode.nock.core.work.WorkerImpl;
import io.machinecode.nock.core.local.LocalRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.work.Worker;
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
    private final Worker executor;
    private final TransactionManager transactionManager;
    private final JobLoader[] jobLoaders;
    private final ArtifactLoader[] artifactLoaders;

    public ConfigurationImpl(final ClassLoader loader) {
        this.loader = loader;
        this.repository = new LocalRepository();
        this.executor = new WorkerImpl();
        this.transactionManager = new LocalTransactionManager();
        this.jobLoaders = JOB_LOADERS;
        this.artifactLoaders = ARTIFACT_LOADERS;
    }

    public ConfigurationImpl(final Configuration configuration) {
        this.loader = configuration.getClassLoader();
        this.repository = configuration.getRepository();
        this.executor = configuration.getExecutor();
        this.transactionManager = configuration.getTransactionManager();
        this.jobLoaders = configuration.getJobLoaders();
        this.artifactLoaders = configuration.getArtifactLoaders();
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
    public Worker getExecutor() {
        return this.executor;
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
}
