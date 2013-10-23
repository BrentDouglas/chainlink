package io.machinecode.nock.spi.configuration;

import io.machinecode.nock.spi.Repository;
import io.machinecode.nock.spi.inject.Injector;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.JobLoader;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RuntimeConfiguration extends Configuration {

    ClassLoader getClassLoader();

    Repository getRepository();

    TransactionManager getTransactionManager();

    JobLoader getJobLoader();

    ArtifactLoader getArtifactLoader();

    Injector getInjector();
}
