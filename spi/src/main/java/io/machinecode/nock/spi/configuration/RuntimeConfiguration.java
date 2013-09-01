package io.machinecode.nock.spi.configuration;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.work.Worker;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RuntimeConfiguration {

    ClassLoader getClassLoader();

    Repository getRepository();

    Worker getWorker();

    TransactionManager getTransactionManager();

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();
}
