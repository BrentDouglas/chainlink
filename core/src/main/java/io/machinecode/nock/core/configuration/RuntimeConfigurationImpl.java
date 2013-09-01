package io.machinecode.nock.core.configuration;

import io.machinecode.nock.core.loader.ArtifactLoaderImpl;
import io.machinecode.nock.core.loader.JobLoaderImpl;
import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.RuntimeConfiguration;
import io.machinecode.nock.spi.work.Worker;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class RuntimeConfigurationImpl implements RuntimeConfiguration {

    private final ClassLoader loader;
    private final Repository repository;
    private final Worker worker;
    private final TransactionManager transactionManager;
    private final JobLoader jobLoader;
    private final ArtifactLoader artifactLoader;

    public RuntimeConfigurationImpl(final Configuration configuration) {
        this.loader = configuration.getClassLoader();
        this.repository = configuration.getRepository();
        this.worker = configuration.getExecutor();
        this.transactionManager = configuration.getTransactionManager();
        this.jobLoader = new JobLoaderImpl(configuration);
        this.artifactLoader = new ArtifactLoaderImpl(configuration);
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
    public Worker getWorker() {
        return this.worker;
    }

    @Override
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
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
