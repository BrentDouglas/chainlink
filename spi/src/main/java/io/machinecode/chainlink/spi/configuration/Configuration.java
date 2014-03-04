package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.ExecutionRepository;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;

import javax.transaction.TransactionManager;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Configuration {

    ClassLoader getClassLoader();

    ExecutionRepository getRepository();

    TransactionManager getTransactionManager();

    JobLoader[] getJobLoaders();

    ArtifactLoader[] getArtifactLoaders();

    Injector[] getInjectors();
}
